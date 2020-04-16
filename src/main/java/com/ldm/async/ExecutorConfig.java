package com.ldm.async;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class ExecutorConfig {
    private static Logger logger = LogManager.getLogger(ExecutorConfig.class.getName());

    @Bean
    public Executor asyncServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        /**
         * 核心线程数设置为核心数+1?
         * 即使当CPU密集型的线程偶尔由于页确实故障或者其他原因而暂停时,这个"额外"的线程也能确保CPU的时钟周期不会被浪费.
         */
        executor.setCorePoolSize(5);
        //配置最大线程数
        /**
         * 线程池大小 = （（线程 IO time + 线程 CPU time ）/线程 CPU time ） CPU数目
         */
        executor.setMaxPoolSize(10);
        //配置队列大小
        executor.setQueueCapacity(400);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("async-service-");
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }
}