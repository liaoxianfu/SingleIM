package com.liao.im.server.netty.service;

import com.liao.im.common.proto.MsgProto;
import com.liao.im.server.session.ServerSession;
import com.liao.im.server.session.SessionMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 聊天处理
 *
 * @author liao
 * create at 2022:03:02  10:07
 */
@Slf4j
@Component
public class ChatProcessor implements Processor {
    @Override
    public Boolean process(ServerSession session, MsgProto.Message msg) {
        final String toUid = msg.getMessageRequest().getTo();
        final Set<ServerSession> sessions = SessionMap.INSTANCE.getSessionsByUserId(toUid);
        if (sessions.size() == 0) {
            log.info("用户没有上线 将消息缓存");
            return false;
        }
        sessions.forEach(s -> s.writeAndFlush(msg));
        return true;
    }
}
