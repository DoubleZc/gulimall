package com.zcx.gulimall.seckill.scheduled;


import com.zcx.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;




/**
 * 每天晚上3点：上架最近三天需要秒杀的商品
 */
@Service
@Slf4j
public class SeckillSkuScheduled
{
	@Autowired
	SeckillService seckillService;
	
	
	@Autowired
	RedissonClient redissonClient;
	
	private final String UP_SKU_LOCK="seckill:uplock:lock";
	
	
	@Scheduled(cron = "0 0 3 * * ?")
	@Async
	public void upSku()
	{
		log.info("定时任务启动");
		RLock lock = redissonClient.getLock(UP_SKU_LOCK);
		lock.lock();
		try {
			seckillService.upSkuLast3Day();
		} finally {
			lock.unlock();
		}
	}

}
