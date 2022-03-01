package com.liao.im.server.session;

import cn.hutool.core.lang.UUID;
import com.liao.im.common.entity.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author liao
 * create at 2022:03:01  11:33
 */
@Data
@Slf4j
public class ServerSession {
    public static final AttributeKey<ServerSession> SESSION_KEY = AttributeKey.newInstance("SESSION_KEY");
    private Channel channel;
    private User user;
    private String sessionId;
    private boolean isLogin = false;
    final static SessionMap sessionMap = SessionMap.INSTANCE;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerSession that = (ServerSession) o;
        return isLogin == that.isLogin && Objects.equals(channel, that.channel) && Objects.equals(user, that.user) && Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel, user, sessionId, isLogin);
    }

    public ServerSession(Channel channel) {
        this.channel = channel;
        this.sessionId = UUID.randomUUID().toString(true);
    }

    public static void closeSession(ChannelHandlerContext ctx) {
        final Channel channel = ctx.channel();
        final ServerSession serverSession = channel.attr(SESSION_KEY).get();
        if (serverSession == null) {
            // 如果
            channel.close().addListener(listener -> {
                final String s = listener.isSuccess() ? "成功" : "失败";
                log.info(s);
            });
            log.error("session 为 null");
            return;
        }
        serverSession.close();
        sessionMap.removeSession(serverSession.getSessionId());
    }

    public void bind() {
        log.debug("server session 绑定会话 {}", channel.remoteAddress());
        channel.attr(SESSION_KEY).set(this);
        sessionMap.addSession(this);
        isLogin = true;
    }

    public void unbind() {
        isLogin = false;
        sessionMap.removeSession(sessionId);
        close();
    }

    public synchronized void close() {
        channel.close().addListener((ChannelFutureListener) f -> {
            if (!f.isSuccess()) {
                log.error("关闭出错");
            } else {
                log.info("关闭成功");
            }
        });
    }

    public synchronized void writeAndFlush(Object msg) {
        if (channel.isWritable()) {
            channel.writeAndFlush(msg).addListener(f -> {
                String info = f.isSuccess() ? "发送成功" : "发送失败";
                log.debug(info);
            });
        } else {
            log.error("通道繁忙，无法写入 缓存消息 等待发送");
        }
    }




}
