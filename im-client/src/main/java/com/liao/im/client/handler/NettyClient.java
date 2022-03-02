package com.liao.im.client.handler;

import com.google.protobuf.ByteString;
import com.liao.im.client.controller.ClientSession;
import com.liao.im.common.config.IMConfig;
import com.liao.im.common.entity.User;
import com.liao.im.common.proto.MsgBuilder;
import com.liao.im.common.proto.MsgProto;
import com.liao.im.common.proto.MsgProto.LoginRequest;
import com.liao.im.common.proto.MsgProto.Message;
import com.liao.im.common.proto.MsgProto.MessageRequest;
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
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
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
    private GenericFutureListener<? extends Future<? super Void>> listener;
    Scanner scanner;

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
                        pipeline.addLast(new IdleStateHandler(0, 9, 0));
                        pipeline.addLast(EchoHandler.INSTANCE);
                        pipeline.addLast(new HeartbeatHandler());
                    }
                });
        scanner = new Scanner(System.in);

        final ChannelFuture future = b.connect();
        while (true) {
            System.out.println("选择操作 1 登录 2 发送消息");
            final int chose = scanner.nextInt();
            switch (chose) {
                case 1:
                    loginUI(future);
                    break;
                case 2:
                    chatUI(future);
            }
        }
    }

    private void loginUI(ChannelFuture future) {
        System.out.println("请输入用户id");
        final String userId = scanner.next();
        System.out.println("请输入密码:");
        final String password = scanner.next();
        System.out.println("请输入类型");
        final int type = scanner.nextInt();
        final User user = new User(userId, userId, IMConfig.platform(type));
        final ClientSession clientSession = new ClientSession(future.channel());
        clientSession.setUser(user); // 应该是得到成功响应后 从响应的数据中获取信息
        final LoginRequest loginRequest = LoginRequest.newBuilder().setPlatform(type)
                .setUid(userId)
                .setAppVersion("v1.0")
                .setToken(password)
                .setDeviceId("NO.001").build();
        final Message message = MsgBuilder.loginRequestMessageBuild(loginRequest, System.currentTimeMillis());
        writeAndFlush(future, message);
    }

    private void writeAndFlush(ChannelFuture future, Message message) {
        future.channel().writeAndFlush(message).addListener(listener -> {
            if (listener.isSuccess()) {
                System.out.println("发送成功");
            } else {
                System.out.println("发送失败");
            }
        });
    }

    private void chatUI(ChannelFuture future) {
        final ClientSession clientSession = future.channel().attr(ClientSession.SESSION_KEY).get();
        if (!clientSession.isLogin()) {
            log.error("没有登录 请进行登录");
            return;
        }
        System.out.print("请输入要发送用户id");
        final String userId = scanner.next();
        System.out.println();
        System.out.print("请输入消息内容");
        final String context = scanner.next();
        System.out.println();
        final User user = clientSession.getUser();

        var messageRequest = MessageRequest.newBuilder().setMsgType(MsgProto.MessageType.TEXT)
                .setContent(ByteString.copyFrom(context.getBytes(StandardCharsets.UTF_8)))
                .setFrom(user.getUserID())
                .setFromNick(user.getNickName())
                .setTo(userId)
                .setTime(System.currentTimeMillis()).build();

        final Message message = MsgBuilder.messageRequestMessageBuild(messageRequest, clientSession.getSessionId(),
                System.currentTimeMillis());
        writeAndFlush(future, message);
    }

    public void close() {
        if (g != null) {
            g.shutdownGracefully();
        }
    }

    public void run() {
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
}
