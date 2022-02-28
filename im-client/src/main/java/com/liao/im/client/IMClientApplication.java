package com.liao.im.client;

import com.liao.im.client.handler.NettyClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author liao
 * create at 2022:02:28  10:37
 */
@SpringBootApplication
public class IMClientApplication {
    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(IMClientApplication.class, args);
        final NettyClient client = context.getBean(NettyClient.class);
        client.run();
    }
}
