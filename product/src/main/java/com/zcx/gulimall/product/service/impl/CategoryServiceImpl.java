package com.zcx.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import com.zcx.gulimall.product.service.CategoryBrandRelationService;
import com.zcx.gulimall.product.vo.Catalog2Vo;
import org.apache.logging.log4j.util.Strings;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.common.utils.PageUtils;
import com.zcx.common.utils.Query;

import com.zcx.gulimall.product.dao.CategoryDao;
import com.zcx.gulimall.product.entity.CategoryEntity;
import com.zcx.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService
{
	@Autowired
	@Lazy
	private CategoryBrandRelationService categoryBrandRelationService;


	@Autowired
	StringRedisTemplate stringRedisTemplate;


	@Autowired
	RedissonClient redisson;


	/***
	 * @CachePut    双写模式         将返回值存入缓存中一份
	 * @CacheEvict  缓存失效模式      更新删除缓存
	 *
	 *
	 *@Caching 合并操作
	 * @Caching(evict = {
	 *      @CacheEvict(value = {"category"},key = "'listRoot'"),
	 *      @CacheEvict(value = {"category"},key = "'getCatalogJson'")
	 *                  })
	 *
	 *
	 *@CacheEvict(value = {"category"}, allEntries = true)
	 * allEntries = true  删除category分区下的所有数据
	 * @param category
	 */


	@CacheEvict(value = {"category"}, allEntries = true)
	@Override
	@Transactional
	public void updateDetail(CategoryEntity category)
	{
		updateById(category);
		categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());


	}

	/***
	 * @Cacheable(value = {"categorys"},key ="#root.method.name" )
	 * key若为字符串 里面需要加‘ ’
	 *
	 *
	 * @param index
	 * @return
	 */

	@Cacheable(value = {"category"}, key = "#root.method.name",sync = true)
	@Override
	public List<CategoryEntity> listRoot(Integer index)
	{
		System.out.println("调用方法");
		return list(new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getParentCid, index));
	}


	public Map<Long, List<Catalog2Vo>> getCatalogJson2()
	{
		String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
		if (Strings.isNotEmpty(catalogJson)) {
			return JSON.parseObject(catalogJson, new TypeReference<Map<Long, List<Catalog2Vo>>>()
			{
			});
		} else {

			return getCatalogJsonFromDb();

		}
	}


	@Cacheable(value = "category", key = "#root.method.name")
	@Override
	public Map<Long, List<Catalog2Vo>> getCatalogJson()
	{
		System.out.println("查询了数据库");
		List<CategoryEntity> all = list();
		List<CategoryEntity> entities = getCategoryEntities(0L, all);
		return entities.stream().collect(Collectors.toMap(CategoryEntity::getCatId, v -> {
					List<CategoryEntity> list = getCategoryEntities(v.getCatId(), all);
					List<Catalog2Vo> collect2 = null;
					if (list != null) {
						collect2 = list.stream().map(i -> {
							List<CategoryEntity> entities1 = getCategoryEntities(i.getCatId(), all);
							List<Catalog2Vo.Catalog3Vo> collect3 = null;
							if (entities1 != null) {
								collect3 = entities1.stream().map(item -> {
									return new Catalog2Vo.Catalog3Vo(i.getCatId().toString(), item.getCatId().toString(), item.getName());
								}).collect(Collectors.toList());
							}
							return new Catalog2Vo(v.getCatId().toString(), collect3, i.getCatId().toString(), i.getName());
						}).collect(Collectors.toList());
					}
					return collect2;
				}
		));
	}


	//查询数据库，存入缓存
	private Map<Long, List<Catalog2Vo>> getLongListMap()
	{
		System.out.println("查询了数据库");
		List<CategoryEntity> all = list();
		List<CategoryEntity> entities = getCategoryEntities(0L, all);
		Map<Long, List<Catalog2Vo>> collect = entities.stream().collect(Collectors.toMap(CategoryEntity::getCatId, v -> {
					List<CategoryEntity> list = getCategoryEntities(v.getCatId(), all);
					List<Catalog2Vo> collect2 = null;
					if (list != null) {
						collect2 = list.stream().map(i -> {
							List<CategoryEntity> entities1 = getCategoryEntities(i.getCatId(), all);
							List<Catalog2Vo.Catalog3Vo> collect3 = null;
							if (entities1 != null) {
								collect3 = entities1.stream().map(item -> {
									return new Catalog2Vo.Catalog3Vo(i.getCatId().toString(), item.getCatId().toString(), item.getName());
								}).collect(Collectors.toList());
							}
							return new Catalog2Vo(v.getCatId().toString(), collect3, i.getCatId().toString(), i.getName());
						}).collect(Collectors.toList());
					}
					return collect2;
				}
		));

		String s = JSON.toJSONString(collect);
		stringRedisTemplate.opsForValue().set("catalogJson", s, 1, TimeUnit.DAYS);
		return collect;
	}

	//redisson分布式锁
	public Map<Long, List<Catalog2Vo>> getCatalogJsonFromDb()
	{
		RLock lock = redisson.getLock("my-lock");
		lock.lock();
		String s = stringRedisTemplate.opsForValue().get("catalogJson");
		if (Strings.isNotEmpty(s)) {
			return JSON.parseObject(s, new TypeReference<Map<Long, List<Catalog2Vo>>>()
			{
			});
		}

		Map<Long, List<Catalog2Vo>> longListMap;
		try {
			longListMap = getLongListMap();
		} finally {
			lock.unlock();
		}
		return longListMap;

	}


	//分布式锁
/*
	public Map<Long, List<Catalog2Vo>> getCatalogJsonFromDb()
	{
		//设置uuid
		String uuid = UUID.randomUUID().toString();
		//在redis中存锁，看是否有锁，设置失效时间，避免死锁
		Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
		if (lock) {

			//无锁，上锁成功
			Map<Long, List<Catalog2Vo>> longListMap = null;
			try {
				//进行业务逻辑，将数据存入redis中
				longListMap = getLongListMap();
			} finally {
				//执行完业务逻辑后，删除所
				//使用lua脚本，保证执行语句的原子性，使用lua脚本删除指定的key，value
				String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
				Long execute = stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
						Collections.singletonList("lock"), uuid);
			}
			return longListMap;
		} else {

			//有锁，上锁失败
			try {
				//等待200毫秒
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//回调方法，看redis中有无数据
			return getCatalogJson();
		}
	}
*/


	//本地锁
/*	public Map<Long, List<Catalog2Vo>> getCatalogJsonFromDb()
	{
		synchronized (this) {
			String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
			if (Strings.isNotEmpty(catalogJson)) {
				return JSON.parseObject(catalogJson, new TypeReference<Map<Long, List<Catalog2Vo>>>()
				{
				});
			}

			System.out.println("查询了数据库");
			List<CategoryEntity> all = list();
			List<CategoryEntity> entities = getCategoryEntities(0L, all);
			Map<Long, List<Catalog2Vo>> collect = entities.stream().collect(Collectors.toMap(CategoryEntity::getCatId, v -> {
						List<CategoryEntity> list = getCategoryEntities(v.getCatId(), all);
						List<Catalog2Vo> collect2 = null;
						if (list != null) {
							collect2 = list.stream().map(i -> {
								List<CategoryEntity> entities1 = getCategoryEntities(i.getCatId(), all);
								List<Catalog2Vo.Catalog3Vo> collect3 = null;
								if (entities1 != null) {
									collect3 = entities1.stream().map(item -> {
										return new Catalog2Vo.Catalog3Vo(i.getCatId().toString(), item.getCatId().toString(), item.getName());
									}).collect(Collectors.toList());
								}
								return new Catalog2Vo(v.getCatId().toString(), collect3, i.getCatId().toString(), i.getName());
							}).collect(Collectors.toList());
						}
						return collect2;
					}
			));

			String s = JSON.toJSONString(collect);
			stringRedisTemplate.opsForValue().set("catalogJson", s, 1, TimeUnit.DAYS);
			return collect;


		}


	}*/

	private List<CategoryEntity> getCategoryEntities(Long catId, List<CategoryEntity> all)
	{
		return all.stream().filter(i -> i.getParentCid().equals(catId)).collect(Collectors.toList());
	}

	@Override
	public Long[] findPath(Long catelogId)
	{

		List<Long> path = new ArrayList<>();
		path.add(catelogId);
		List<Long> init = getPath(catelogId, path);
		Collections.reverse(init);
		return init.toArray(new Long[0]);


	}


	public List<Long> getPath(Long catelogId, List<Long> path)
	{
		CategoryEntity entity = getById(catelogId);
		if (entity.getParentCid() != 0) {

			path.add(entity.getParentCid());
			getPath(entity.getParentCid(), path);

		}
		return path;
	}


	//删除数组
	@Override
	public void removeMenu(List<Long> asList)
	{

		//TODO        检查菜单是否被引用
		removeByIds(asList);


	}

	@Override
	public List<CategoryEntity> listWithTree()
	{
		//查出所有分类
		List<CategoryEntity> categoryEntityList = list();
		List<CategoryEntity> collect0 = getChildren(categoryEntityList, 0L);
		return collect0;
	}

	/****
	 * 查找子类方法
	 * @param categoryEntityList 所有参数
	 * @param selfId 自身
	 * @return
	 */

	private List<CategoryEntity> getChildren(List<CategoryEntity> categoryEntityList, Long selfId)
	{
		return categoryEntityList.stream().
				//找到所有子类
						filter(item -> item.getParentCid() == selfId)
				//用递归将子类的children附上值
				.map(item -> {
							item.setChildren(getChildren(categoryEntityList, item.getCatId()));
							return item;
						}
						//按sort排序
				).sorted((o1, o2) -> (o1.getSort() == null ? 0 : o1.getSort()) - (o2.getSort() == null ? 0 : o2.getSort()))

				.collect(Collectors.toList());

	}


	@Override
	public PageUtils queryPage(Map<String, Object> params)
	{
		IPage<CategoryEntity> page = this.page(
				new Query<CategoryEntity>().getPage(params),
				new QueryWrapper<CategoryEntity>()
		);

		return new PageUtils(page);
	}

}