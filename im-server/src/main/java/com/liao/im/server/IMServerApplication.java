package com.liao.im.server;

import com.liao.im.server.netty.NettyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 启动类
 *
 * @author liao
 * create at 2022:02:27  20:52
 */
@SpringBootApplication
@Slf4j
public class IMServerApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(IMServerApplication.class, args);
        final NettyServer nettyServer = context.getBean(NettyServer.class);
        nettyServer.run();
    }
}
