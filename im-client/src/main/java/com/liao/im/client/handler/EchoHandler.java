package com.liao.im.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liao
 * create at 2022:02:28  11:23
 */
@ChannelHandler.Sharable
@Slf4j
public class EchoHandler extends SimpleChannelInboundHandler<ByteBuf> {
    public final static EchoHandler INSTANCE = new EchoHandler();

    private EchoHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        final int count = msg.readableBytes();
        final byte[] bs = new byte[count];
        msg.readBytes(bs);
        log.info("data {}", new String(bs));
    }

}
