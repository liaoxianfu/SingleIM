package com.liao.im.server.netty.handlers;

import com.liao.im.common.proto.MsgProto;
import com.liao.im.server.netty.service.LoginProcessor;
import com.liao.im.server.session.ServerSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author liao
 * create at 2022:03:01  11:26
 */
@ChannelHandler.Sharable
@Component
@Slf4j
public class LoginHandler extends ChannelInboundHandlerAdapter {

    @Resource
    ThreadPoolExecutor threadPoolExecutor;
    @Resource
    LoginProcessor loginProcessor;

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
        final ServerSession session = new ServerSession(ctx.channel());
        final Future<Boolean> submit = threadPoolExecutor.submit(() -> {
            final Boolean process = loginProcessor.process(session, message);
            log.debug("{} 线程提交任务", Thread.currentThread().getName());
            return process;
        });
        if (submit.get()) {
            log.info("登录成功，进行后续操作");
        } else {
            ServerSession.closeSession(ctx);
        }

    }
}
