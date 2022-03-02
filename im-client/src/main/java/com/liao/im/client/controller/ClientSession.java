package com.liao.im.client.controller;

import com.liao.im.common.entity.User;
import com.liao.im.common.proto.MsgProto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

/**
 * @author liao
 * create at 2022:02:28  21:06
 */
@Slf4j
@Data
public class ClientSession {
    public static final AttributeKey<ClientSession> SESSION_KEY = AttributeKey.valueOf("SESSION_KEY");

    private Channel channel;
    public User user;

    private String sessionId;

    private boolean isConnected = false;
    private boolean isLogin = false;

    public ClientSession(Channel channel) {
        this.channel = channel;
        channel.attr(SESSION_KEY).set(this);
    }

    public static void loginSuccess(ChannelHandlerContext context, MsgProto.Message msg) {
        final Channel channel = context.channel();
        final ClientSession session = channel.attr(SESSION_KEY).get();
        final String sessionId = msg.getSessionId();
        session.setSessionId(sessionId);
        session.setLogin(true);
        log.info("登录成功 session_id {}", sessionId);
    }

    public static ClientSession getSession(ChannelHandlerContext context) {
        return context.channel().attr(SESSION_KEY).get();
    }

    public SocketAddress getRemoteAddress() {
        return channel.remoteAddress();
    }

    // 写数据
    public ChannelFuture writeAndFlush(Object msg) {
        return channel.writeAndFlush(msg);
    }

    public void writeAndClose(Object msg) {
        channel.writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE);
    }

    public void close() {
        isConnected = false;
        channel.close().addListener(listener -> {
            var msg = listener.isSuccess() ? "断开成功" : "断开失败";
            log.info(msg);
        });
    }
}
