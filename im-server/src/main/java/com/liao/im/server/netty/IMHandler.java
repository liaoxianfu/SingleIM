package com.liao.im.server.netty;

import com.liao.im.common.utils.ProtoDupleHandler;
import com.liao.im.server.config.ServerConfig;
import com.liao.im.server.netty.handlers.ExceptionHandler;
import com.liao.im.server.netty.handlers.HearBeatHandler;
import com.liao.im.server.netty.handlers.LoginHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 注册所有的handler
 *
 * @author liao
 * create at 2022:02:27  21:09
 */
@Slf4j
@Component
public class IMHandler extends ChannelInitializer<SocketChannel> {

    @Resource
    private LoginHandler loginHandler;
    @Value("${netty.heartbeat:10}")
    int heartbeat;

    @Resource
    private HearBeatHandler hearBeatHandler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        log.debug("heartbeat={}", heartbeat);

        final ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new ProtoDupleHandler());
        // 增加心跳机制设置
        pipeline.addLast(new IdleStateHandler(10, 0, 0));
        pipeline.addLast(ServerConfig.LOGIN_STR, loginHandler);
        pipeline.addLast(ExceptionHandler.INSTANCE);
        pipeline.addLast(hearBeatHandler);
    }
}
