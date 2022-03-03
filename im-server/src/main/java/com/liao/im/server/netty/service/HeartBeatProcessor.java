package com.liao.im.server.netty.service;

import com.liao.im.common.proto.MsgProto;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author liao
 * create at 2022:03:02  17:57
 */
@Component
@Slf4j
public class HeartBeatProcessor implements Processor {
    @Override
    public Boolean process(ChannelHandlerContext ctx, MsgProto.Message msg) {
        ctx.channel().writeAndFlush(msg).addListener(listener -> {
            if (listener.isSuccess()) {
                log.debug("心跳数据发送成功");
            } else {
                log.debug("心跳数据发送失败");
            }
        });
        return true;
    }
}
