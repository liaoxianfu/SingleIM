package com.liao.im.server.netty.service;

import com.liao.im.common.proto.MsgProto;
import com.liao.im.server.session.ServerSession;

/**
 * @author liao
 * create at 2022:03:02  10:08
 */
public interface Processor {
    Boolean process(ServerSession session, MsgProto.Message msg);
}
