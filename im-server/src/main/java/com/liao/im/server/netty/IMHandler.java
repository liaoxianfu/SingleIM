package com.liao.im.server.netty;

import com.liao.im.common.utils.ProtoDupleHandler;
import com.liao.im.server.config.ServerConfig;
import com.liao.im.server.netty.handlers.ExceptionHandler;
import com.liao.im.server.netty.handlers.LoginHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 注册所有的handler
 *
 * @author liao
 * create at 2022:02:27  21:09
 */
@Component
public class IMHandler extends ChannelInitializer<SocketChannel> {

    @Resource
    private LoginHandler loginHandler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new ProtoDupleHandler());
        pipeline.addLast(ServerConfig.LOGIN_STR, loginHandler);
        pipeline.addLast(ExceptionHandler.INSTANCE);
    }
}
