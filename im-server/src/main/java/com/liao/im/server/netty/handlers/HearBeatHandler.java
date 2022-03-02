package com.liao.im.server.netty.handlers;

import com.liao.im.common.proto.MsgProto;
import com.liao.im.server.netty.service.HeartBeatProcessor;
import com.liao.im.server.netty.thread.HandlerTask;
import com.liao.im.server.session.ServerSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liao
 * create at 2022:03:02  17:01
 */
@Component
@Slf4j
@ChannelHandler.Sharable
public class HearBeatHandler extends ChannelInboundHandlerAdapter {
    @Resource
    private HandlerTask handlerTask;
    @Resource
    private HeartBeatProcessor heartBeatProcessor;
    private final AtomicInteger lossCount = new AtomicInteger(0);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("超过长时间没有收到心跳");
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                if (lossCount.incrementAndGet() > 2) {
                    log.info("超过2个单位时间");
                    ServerSession.closeSession(ctx);
                }
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof MsgProto.Message)) {
            log.error("消息格式出错");
            super.channelRead(ctx, msg);
            return;
        }
        MsgProto.Message message = (MsgProto.Message) msg;
        if (!message.getType().equals(MsgProto.HeadType.KEEPALIVE_REQUEST)) {
            super.channelRead(ctx, msg);
            return;
        }
        log.info("心跳数据");
        log.error("processor = null ? {}",heartBeatProcessor==null);
        log.error("ctx = null ? {}",ctx==null);
        log.error("message = null ? {}",message==null);
        final Future<Boolean> handle = handlerTask.handle(heartBeatProcessor, ctx, message);
        if (handle.get()) {
            log.info("转发成功");
        } else {
            log.info("转发失败");
        }
    }
}
