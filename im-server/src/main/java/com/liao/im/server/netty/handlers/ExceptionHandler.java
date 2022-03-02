package com.liao.im.server.netty.handlers;

import com.liao.im.server.session.ServerSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liao
 * create at 2022:03:01  19:36
 */
@Slf4j
@ChannelHandler.Sharable
public class ExceptionHandler extends ChannelInboundHandlerAdapter {
    public static ExceptionHandler INSTANCE = new ExceptionHandler();

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        // 移除channel时 关闭连接
        ServerSession.closeSession(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("出现异常");
        super.exceptionCaught(ctx, cause);
    }
}
