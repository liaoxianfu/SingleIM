package com.liao.im.server.netty.handlers;

import com.liao.im.common.proto.MsgProto;
import com.liao.im.common.proto.MsgProto.Message;
import com.liao.im.server.netty.service.ChatProcessor;
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
 * create at 2022:03:01  21:02
 */
@Component
@Slf4j
@ChannelHandler.Sharable
public class ChatHandler extends ChannelInboundHandlerAdapter {

    @Resource
    private HandlerTask task;
    @Resource
    private ChatProcessor processor;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof Message)) {
            log.error("消息格式出错");
            super.channelRead(ctx, msg);
            return;
        }
        Message message = (Message) msg;
        if (!message.getType().equals(MsgProto.HeadType.MESSAGE_REQUEST)) {
            log.info("该消息不是消息请求");
            super.channelRead(ctx, msg);
            return;
        }
        // 验证用户是否登录
        final ServerSession serverSession = ctx.channel().attr(ServerSession.SESSION_KEY).get();
        if (serverSession == null || !serverSession.isLogin()) {
            log.error("用户还没登录，请登录");
            return;
        }
        // 数据发送
        final Future<Boolean> handle = task.handle(processor, ctx, message);
        if (handle.get()) {
            log.info("发送成功");
        } else {
            log.error("发送失败");
        }
    }
}
