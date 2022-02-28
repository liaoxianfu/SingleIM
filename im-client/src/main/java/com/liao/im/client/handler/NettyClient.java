package com.liao.im.client.handler;

import com.liao.im.common.config.IMConfig;
import com.liao.im.common.utils.DataPackUtil;
import com.liao.im.common.utils.ProtoDupleHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
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

    public void run() {
        log.debug("配置信息 remoteHost:{} port:{} workerThreads:{}", remoteHost, port, workerThreads);
        try {
            connect();
        } catch (Exception e) {
            log.error("出现错误{}", e.getMessage());
        } finally {
            close();
        }
    }

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
        final ChannelFuture future = b.connect();
        future.addListener(listener -> {
            log.debug(listener.isSuccess() ? "连接成功" : "连接失败");
        });
        String msg;
        var channel = future.channel();
        final Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            msg = scanner.next();
            if (msg.equals("qq")) {
                throw new Exception("close");
            }
            log.debug("msg={}", msg);
            channel.writeAndFlush(msg).addListener(listener -> {
                final String s = listener.isSuccess() ? "发送成功" : "发送失败";
                log.debug(s);
            });
        }
        future.channel().closeFuture().sync();
    }

    public void close() {
        if (g != null) {
            g.shutdownGracefully();
        }
    }
}
