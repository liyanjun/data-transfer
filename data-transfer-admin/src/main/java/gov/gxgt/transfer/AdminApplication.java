/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package gov.gxgt.transfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executor;

@EnableAsync
@EnableCaching
@SpringBootApplication
public class AdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	/***
	 * 创建异步任务执行器
	 *
	 * @return
	 */
	@Bean("taskExecutor")
	public Executor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		//如果池中的实际线程数小于corePoolSize,无论是否其中有空闲的线程，都会给新的任务产生新的线程
		taskExecutor.setCorePoolSize(10);
		//连接池中保留的最大连接数。
		taskExecutor.setMaxPoolSize(30);
		//queueCapacity 线程池所使用的缓冲队列
		taskExecutor.setQueueCapacity(50000);
		//强烈建议一定要给线程起一个有意义的名称前缀，便于分析日志
		taskExecutor.setThreadNamePrefix("data transfer thread-");
		taskExecutor.initialize();
		return taskExecutor;
	}
}
