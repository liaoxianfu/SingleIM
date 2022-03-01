package com.liao.im.client.handler;

import com.liao.im.common.utils.ProtoDupleHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * @author liao
 * create at 2022:02:28  10:42
 */
@Component
@ConfigurationProperties("netty.client")
@Data
@Slf4j
public class NettyClient {
    private String remoteHost;
    private int port;
    private int workerThreads;
    NioEventLoopGroup g;
    private GenericFutureListener<? extends Future<? super Void>> listener;

    public void connect() throws Exception {
        final Bootstrap b = new Bootstrap();
        g = new NioEventLoopGroup(workerThreads);
        b.group(g).channel(NioSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .remoteAddress(remoteHost, port)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        final ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ProtoDupleHandler());
                        pipeline.addLast(EchoHandler.INSTANCE);

                    }
                });
        b.connect().addListener(listener);
    }

    public void close() {
        if (g != null) {
            g.shutdownGracefully();
        }
    }
}
