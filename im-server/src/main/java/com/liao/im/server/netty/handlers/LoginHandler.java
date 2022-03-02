package com.liao.im.server.netty.handlers;

import com.liao.im.common.proto.MsgProto;
import com.liao.im.server.config.ServerConfig;
import com.liao.im.server.netty.service.LoginProcessor;
import com.liao.im.server.netty.thread.HandlerTask;
import com.liao.im.server.session.ServerSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Future;

/**
 * @author liao
 * create at 2022:03:01  11:26
 */
@ChannelHandler.Sharable
@Component
@Slf4j
public class LoginHandler extends ChannelInboundHandlerAdapter {

    @Resource
    private HandlerTask handlerTask;

    @Resource
    private LoginProcessor loginProcessor;

    @Resource
    private ChatHandler chatHandler;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if ((!(msg instanceof MsgProto.Message))) {
            super.channelRead(ctx, msg);
            return;
        }
        final MsgProto.Message message = (MsgProto.Message) msg;
        if (!message.getType().equals(MsgProto.HeadType.LOGIN_REQUEST)) {
            super.channelRead(ctx, msg);
            return;
        }
        final Future<Boolean> result = handlerTask.handle(loginProcessor, ctx, message);
        if (result.get()) {
            log.info("登录成功，进行后续操作");
            // 加入聊天业务操作
            ctx.pipeline().addAfter(ServerConfig.LOGIN_STR, ServerConfig.CHAT_STR, chatHandler);
            ctx.pipeline().remove(ServerConfig.LOGIN_STR);
        } else {
            log.info("登录失败");
            super.channelRead(ctx, msg);
        }

    }
}
