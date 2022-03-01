package com.liao.im.server.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author liao
 * create at 2022:03:01  16:11
 */
@ConfigurationProperties("thead.pool")
@Configuration
@Data
@Slf4j
public class ThreadPoolConfig {
    private int coreSize;
    private int maxSize;
    private long keepAliveSeconds;
    private int blockingQueueSize;

    @Bean("threadPoolExecutor")
    public ThreadPoolExecutor threadPoolExecutor() {

        final ThreadPoolExecutor executor = new ThreadPoolExecutor(coreSize, maxSize, keepAliveSeconds,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(blockingQueueSize));
        log.debug("创建线程池成功 参数为 coreSize:{} maxSize:{} keepAliveSeconds:{} blockingQueueSize:{}",
                coreSize, maxSize, keepAliveSeconds, blockingQueueSize);
        return executor;
    }
}
