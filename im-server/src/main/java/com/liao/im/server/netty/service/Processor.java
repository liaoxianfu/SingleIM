package com.liao.im.server.netty.service;

import com.liao.im.common.proto.MsgProto;
import com.liao.im.server.session.ServerSession;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author liao
 * create at 2022:03:02  10:08
 */
@FunctionalInterface
public interface Processor {
    Boolean process(ChannelHandlerContext ctx, MsgProto.Message msg);
}
