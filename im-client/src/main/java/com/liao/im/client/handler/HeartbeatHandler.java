package com.liao.im.client.handler;

import com.liao.im.client.controller.ClientSession;
import com.liao.im.common.proto.MsgBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liao
 * create at 2022:03:02  17:41
 */
@Component
@Slf4j
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
    private AtomicInteger errorCount = new AtomicInteger();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("超过长时间没有收到心跳");
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
//                final String sessionId = ClientSession.getSession(ctx).getSessionId();
                ctx.writeAndFlush(MsgBuilder.heartbeatMessageBuild(System.currentTimeMillis()))
                        .addListener(listener -> {
                            if (!listener.isSuccess()) {
                                errorCount.incrementAndGet();
                            } else {
                                log.info("发送心跳数据包");
                            }
                        });
            }
            if (errorCount.get() > 2) {
                final ClientSession clientSession = ctx.channel().attr(ClientSession.SESSION_KEY).get();
                log.info("关闭连接");
                clientSession.close();
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
