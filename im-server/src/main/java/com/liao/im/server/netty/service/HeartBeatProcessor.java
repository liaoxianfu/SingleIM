package com.liao.im.server.netty.service;

import com.liao.im.common.proto.MsgProto;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

/**
 * @author liao
 * create at 2022:03:02  17:57
 */
@Component
public class HeartBeatProcessor implements Processor {
    @Override
    public Boolean process(ChannelHandlerContext ctx, MsgProto.Message msg) {
        ctx.channel().writeAndFlush(msg);
        return true;
    }
}
