package com.liao.im.server.netty;

import com.liao.im.common.utils.ProtoDupleHandler;
import com.liao.im.server.netty.handlers.DemoHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * 注册所有的handler
 *
 * @author liao
 * create at 2022:02:27  21:09
 */
public class IMHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new ProtoDupleHandler());
        pipeline.addLast(DemoHandler.INSTANCE);
    }
}
