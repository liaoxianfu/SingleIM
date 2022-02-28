package com.liao.im.server.netty.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author liao
 * create at 2022:02:27  21:19
 */
@ChannelHandler.Sharable
@Slf4j
public class DemoHandler extends ChannelInboundHandlerAdapter {
    private DemoHandler() {
    }

    public static final DemoHandler INSTANCE = new DemoHandler();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.debug("客户端{}上线", ctx.channel().remoteAddress());
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.debug("服务器移除客户端{}连接", ctx.channel().remoteAddress());
        super.handlerRemoved(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("客户端{}连接建立", ctx.channel().remoteAddress());
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("客户端{}连接断开", ctx.channel().remoteAddress());
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("客户端{}在ctx中处于活跃状态", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("客户端{}在ctx中处于不活跃状态", ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("客户端{}处于可读状态", ctx.channel().remoteAddress());
//        final ByteBuf buf = (ByteBuf) msg;
//        final byte[] data = new byte[buf.readableBytes()];
//        buf.readBytes(data);
//        final String backMsg = "call back" + new String(data);
//        log.debug("发送的数据{}", backMsg);
//        final ByteBuf buffer = ctx.alloc().buffer();
//        buffer.writeBytes(backMsg.getBytes(StandardCharsets.UTF_8));
//        ctx.writeAndFlush(buffer);
        log.debug("读取的数据为{} 类型为{}", msg, msg.getClass());
        ctx.channel().writeAndFlush(msg);
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.debug("客户端{}处于读取完毕状态", ctx.channel().remoteAddress());
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.debug("客户端{}用户事件被出发", ctx.channel().remoteAddress());
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        log.debug("客户端{}写状态发生改变", ctx.channel().remoteAddress());
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("客户端{}出现异常 异常原因{}", ctx.channel().remoteAddress(), cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }
}
