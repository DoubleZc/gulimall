package com.zcx.gulimall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.zcx.common.comm.CosException;
import com.zcx.common.constant.OrderConstant;
import com.zcx.common.to.MemberTo;
import com.zcx.common.to.mq.MqTo;
import com.zcx.common.to.mq.SeckillTo;
import com.zcx.common.utils.ExceptionCode;
import com.zcx.common.utils.MyThreadMsg;
import com.zcx.common.utils.R;
import com.zcx.gulimall.seckill.feign.CouponFeignService;
import com.zcx.gulimall.seckill.feign.MqFeignService;
import com.zcx.gulimall.seckill.service.SeckillService;
import com.zcx.gulimall.seckill.vo.SeckillSessionVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService
{
	@Autowired
	CouponFeignService couponFeignService;
	
	@Autowired
	StringRedisTemplate redisTemplate;
	
	
	@Autowired
	RedissonClient redissonClient;
	
	
	@Autowired
	MqFeignService mqFeignService;
	
	
	private final String SESSION_PREFIX = "seckill:session:";
	private final String SESSION_SKU_PREFIX = "seckill:sku:";
	private final String SKU_SEMAPHORE = "seckill:stock:";
	
	@Override
	public void upSkuLast3Day()
	{
		R data = null;
		try {
			data = couponFeignService.getSkuLate3Dys();
		} catch (Exception e) {
			log.error("远程调用失败");
			e.printStackTrace();
		}
		
		
		assert data != null;
		if (data.getCode() == 0) {
			List<SeckillSessionVo> list = data.getData("data", new TypeReference<List<SeckillSessionVo>>()
			{
			});
			saveSessionInfo(list);
			saveSessionSkuInfo(list);
			
			log.info("上架成功");
		}else
		{
			log.error(data.get("msg").toString());
		}
		
	}
	
	@Override
	public List<SeckillSessionVo.SeckillSkuRelationVo> getCurrentSeckillSkus()
	{
		log.info("查询数据");
		Date date = new Date();
		long time = date.getTime();
		
		String key="";
		try (Cursor<String> cursor = redisTemplate.scan(ScanOptions.scanOptions().match(SESSION_PREFIX+"*").count(1000).build()))
		{
			while (cursor.hasNext()) {
				String next = cursor.next();
				String replace = next.replace(SESSION_PREFIX, "");
				String[] s = replace.split("_");
				if (Long.parseLong(s[0])<time&&time<Long.parseLong(s[1]))
				{
					key=next;
					break;
				}
			}
		}
		
		List<String> skuIds = redisTemplate.opsForList().range(key, 0, -1);
		
		BoundHashOperations<String, String, Object> operations = redisTemplate.boundHashOps(SESSION_SKU_PREFIX);
		assert skuIds != null;
		List<Object> objects = operations.multiGet(skuIds);
		
		
		
		assert objects != null;
		return objects.stream().map(i -> {
					return JSON.parseObject(i.toString(), SeckillSessionVo.SeckillSkuRelationVo.class);
				}
		).collect(Collectors.toList());
	}
	
	@Override
	public List<SeckillSessionVo.SeckillSkuRelationVo> getseckillInfo(Long skuId)
	{
		BoundHashOperations<String, String, String> operations = redisTemplate.boundHashOps(SESSION_SKU_PREFIX);
		List<SeckillSessionVo.SeckillSkuRelationVo> infos=new ArrayList<>();
		try(Cursor<Map.Entry<String, String>> scan = operations.scan(ScanOptions.scanOptions().match("*" + skuId).build());
		)
		{
			while (scan.hasNext())
			{
				Map.Entry<String, String> next = scan.next();
				String value = next.getValue();
				SeckillSessionVo.SeckillSkuRelationVo seckillSkuRelationVo = JSON.parseObject(value, SeckillSessionVo.SeckillSkuRelationVo.class);
				if (new Date().getTime()<seckillSkuRelationVo.getStartTime())
				{
					seckillSkuRelationVo.setUuid("");
					infos.add(seckillSkuRelationVo);
					
				
				}else if (new Date().getTime()<=seckillSkuRelationVo.getEndTime())
				{
					infos.add(seckillSkuRelationVo);
				}
			}
		}
		infos.sort(Comparator.comparingLong(SeckillSessionVo.SeckillSkuRelationVo::getStartTime));
		return infos;
	}
	
	@Override
	public String kill(String killId, Integer count, String code) throws CosException
	{
		BoundHashOperations<String, String, String> operations = redisTemplate.boundHashOps(SESSION_SKU_PREFIX);
		String voJson = operations.get(killId);
		if (!Strings.isNotEmpty(voJson))
		{
			return null;
		}
		SeckillSessionVo.SeckillSkuRelationVo vo = JSON.parseObject(voJson, SeckillSessionVo.SeckillSkuRelationVo.class);
		Long startTime = vo.getStartTime();
		Long endTime = vo.getEndTime();
		long now = new Date().getTime();
		if (vo.getSeckillLimit()<count)
		{
			return  null;
		}
		if (now>endTime||now<startTime)
		{
			return null;
		}
		if (!vo.getUuid().equals(code))
		{
			return null;
		}
		MemberTo msg = MyThreadMsg.getMsg(MemberTo.class);
		Long id = msg.getId();
		
		
		String key="seckill:memberlock:"+id+"_"+killId+"_"+code+"_"+count;
		
		Boolean canBuy = redisTemplate.opsForValue().setIfAbsent(key, count.toString(), endTime - now, TimeUnit.MILLISECONDS);
		if (!canBuy)
		{
			return null;
		}
		RSemaphore semaphore = redissonClient.getSemaphore(SKU_SEMAPHORE + code);
		
		boolean isSuccess = false;
		try {
			isSuccess = semaphore.tryAcquire(count,100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			return null;
		}
		if (!isSuccess)
		{
			return null;
		}
		String timeId = IdWorker.getTimeId();
		SeckillTo seckillTo = new SeckillTo();
		seckillTo.setOrderSn(timeId);
		seckillTo.setSeckillPrice(vo.getSeckillPrice());
		seckillTo.setNum(count);
		seckillTo.setSkuId(vo.getSkuId());
		seckillTo.setPromotionSessionId(vo.getPromotionSessionId());
		seckillTo.setMemberId(msg.getId());
		
		try {
			mqFeignService.sendMessage(new MqTo(OrderConstant.OrderMQ.EXCHANGE,OrderConstant.RouteKey.TO_SECKILL_QUEUE.key,seckillTo));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeId;
		
	}
	
	
	private void saveSessionInfo(List<SeckillSessionVo> vos)
	{
		
		
		vos.forEach(vo ->
		{
			Date startTime = vo.getStartTime();
			Date endTime = vo.getEndTime();
			String key = SESSION_PREFIX + startTime.getTime() + "_" + endTime.getTime();
			Boolean aBoolean = redisTemplate.hasKey(key);
			assert aBoolean != null;
			if (!aBoolean) {
				List<String> skus = vo.getSkuRelationEntities().stream().map(i -> vo.getId()+"_"+i.getSkuId().toString()).collect(Collectors.toList());
				redisTemplate.opsForList().leftPushAll(key, skus);
			}
		});
		
		
	}
	
	
	private void saveSessionSkuInfo(List<SeckillSessionVo> vos)
	{
		BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(SESSION_SKU_PREFIX);
		
		vos.forEach(vo -> {
			vo.getSkuRelationEntities().forEach(seckillSkuRelationVo -> {
				seckillSkuRelationVo.setStartTime(vo.getStartTime().getTime());
				seckillSkuRelationVo.setEndTime(vo.getEndTime().getTime());
				String uuid = UUID.randomUUID().toString().replace("-", "");
				seckillSkuRelationVo.setUuid(uuid);
				String key = vo.getId() + "_" + seckillSkuRelationVo.getSkuId().toString();
				Boolean aBoolean = operations.hasKey(key);
				assert  aBoolean!= null;
				if (!aBoolean)
				{
					//分布式信号量
					RSemaphore semaphore = redissonClient.getSemaphore(SKU_SEMAPHORE + uuid);
					//秒杀件数
					semaphore.trySetPermits(seckillSkuRelationVo.getSeckillCount());
					
					
					operations.put(key, JSON.toJSONString(seckillSkuRelationVo));
				}
				
			});
		});
		
		
	}
	
}
