package com.zcx.gulimall.product.web;


import com.zcx.gulimall.product.entity.CategoryEntity;
import com.zcx.gulimall.product.service.CategoryService;
import com.zcx.gulimall.product.vo.Catalog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Controller
public class IndexController
{
	@Autowired
	CategoryService categoryService;

	@Autowired
	RedissonClient redissonClient;

	@GetMapping({"/", "/index.html"})
	public String indexPage(Model model)
	{
		List<CategoryEntity> entities = categoryService.listRoot(0);


		model.addAttribute("category", entities);

		return "index";
	}



	@ResponseBody
	@GetMapping("/index/json/catalog.json")
	public Map<Long, List<Catalog2Vo>> getCatalogJson()
	{

		Map<Long, List<Catalog2Vo>> map = categoryService.getCatalogJson();
		return map;


	}


	@ResponseBody
	@GetMapping("/hello")
	public String test()
	{
		RLock lock = redissonClient.getLock("my-mylock");
		lock.lock();
		try{
			System.out.println("加锁");
			Thread.sleep(30000);

		}catch (Exception e)
		{
		}
		finally {

			System.out.println("释放锁");
			lock.unlock();
		}
		return "hello";
	}


	//读写锁
	//读读锁 可以并发进行 相当于无锁
	//读锁 写锁  和 写锁 写锁  需要等待前者释放锁
	@ResponseBody
	@GetMapping("/read")
	public String read()
	{
		RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("my-mylock");
		RLock rLock = readWriteLock.readLock();
		try{
			rLock.lock();
			System.out.println("读加锁");
			Thread.sleep(30000);
		}catch (Exception e)
		{
		}
		finally {
			System.out.println("读释放锁");
			rLock.unlock();
		}
		return "hello";
	}


	@ResponseBody
	@GetMapping("/write")
	public String write()
	{
		RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("my-mylock");
		RLock rLock = readWriteLock.writeLock();
		try{
			rLock.lock();
			System.out.println("写加锁");
			Thread.sleep(30000);
		}catch (Exception e)
		{
		}
		finally {
			System.out.println("写释放锁");
			rLock.unlock();
		}
		return "hello";
	}





	//信号量（Semaphore）
	@ResponseBody
	@GetMapping("/park")
	public String park() throws InterruptedException
	{
		RSemaphore park = redissonClient.getSemaphore("park");
		park.acquire();
		return "a";
	}


	@ResponseBody
	@GetMapping("/go")
	public String go() throws InterruptedException
	{
		RSemaphore park = redissonClient.getSemaphore("park");
		park.release();
	    return "a";
	}

//闭锁
	@ResponseBody
	@GetMapping("/CountDownLatch")
	public String CountDownLatch() throws InterruptedException
	{
		RCountDownLatch latch = redissonClient.getCountDownLatch("anyCountDownLatch");
		latch.trySetCount(1);
		latch.await();

		// 在其他线程或其他JVM里
//		RCountDownLatch latch = redissonClient.getCountDownLatch("anyCountDownLatch");
//		latch.countDown();


		return "a";
	}














}
