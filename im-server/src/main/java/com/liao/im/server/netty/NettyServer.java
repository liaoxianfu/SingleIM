package com.liao.im.server.netty;

import com.liao.im.server.session.SessionMap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author liao
 * create at 2022:02:27  21:04
 */
@Component
@Slf4j
@Data
@ConfigurationProperties(prefix = "netty.server")
public class NettyServer {
    private int workThread;
    private int port;
    @Resource
    private IMHandler imHandler;

    public void run() {
        final ServerBootstrap b = new ServerBootstrap();
        log.debug("work  thread :{} netty port: {}", workThread, port);
        final NioEventLoopGroup boosGroup = new NioEventLoopGroup(1);
        setWorkerThread();
        final NioEventLoopGroup workGroup = new NioEventLoopGroup(workThread);
        try {
            ChannelFuture future = b.group(boosGroup, workGroup)
                    .childHandler(imHandler)
                    .channel(NioServerSocketChannel.class)
                    .bind(port);
            log.debug("netty 初始化成功");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 清理所有的连接
            SessionMap.INSTANCE.closeAll();
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    private void setWorkerThread() {
        if (workThread < 0) {
            log.error("工作线程设置小于0 修改为netty默认线程数");
            workThread = 0;
        }
    }


}
