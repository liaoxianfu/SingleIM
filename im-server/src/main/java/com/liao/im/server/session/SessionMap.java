package com.liao.im.server.session;

import com.liao.im.common.entity.User;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liao
 * create at 2022:03:01  11:54
 */
@Slf4j
public class SessionMap {
    private SessionMap() {
    }

    public static final SessionMap INSTANCE = new SessionMap();
    private static final ConcurrentHashMap<String, ServerSession> sessionIdMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Set<ServerSession>> userIdSessionMap = new ConcurrentHashMap<>();

    public void addSession(ServerSession session) {
        final String sessionId = session.getSessionId();
        final String userID = session.getUser().getUserID();
        sessionIdMap.put(sessionId, session);
        log.debug("sessionIdMap 加入用户信息 当前存在session数量 {}", sessionIdMap.size());
        if (userIdSessionMap.containsKey(userID)) {
            final Set<ServerSession> sessionSet = userIdSessionMap.get(userID);
            sessionSet.add(session);
            log.debug("userIdSessionMap 存在该用户id 添加新的登录平台 当前用户{} session数量为{}", userID, sessionSet.size());
        } else {
            final HashSet<ServerSession> sessions = new HashSet<>();
            sessions.add(session);
            userIdSessionMap.put(userID, sessions);
            log.debug("userIdSessionMap 不存在该用户id 添加新的set集合");
        }
    }

    public ServerSession getSession(String sessionId) {
        return sessionIdMap.get(sessionId);
    }

    public Set<ServerSession> getSessionsByUserId(String userId) {
        return userIdSessionMap.get(userId);
    }

    public void removeSession(String sessionId) {
        final ServerSession session = sessionIdMap.get(sessionId);
        final User user = session.getUser();
        final String userID = user.getUserID();
        final String platform = user.getPlatform();
        final Set<ServerSession> sessionSet = userIdSessionMap.get(userID);
        final Iterator<ServerSession> iterator = sessionSet.iterator();
        while (iterator.hasNext()) {
            final ServerSession ss = iterator.next();
            if (ss.getUser().getUserID().equals(userID) && ss.getUser().getPlatform().equals(platform)) {
                log.debug("删除数据");
                iterator.remove();
            }
        }

        log.debug("成功移除userIdSessionMap 用户{} 该sessionId下的session 数量为{}", userID, sessionSet.size());
        sessionIdMap.remove(sessionId);
        log.debug("成功移除sessionIdMap 剩余{}", sessionIdMap.size());
    }

    /**
     * 判断该平台下的用户是否登录
     *
     * @param user 用户
     * @return 是否登录
     */
    public boolean hasLoginInThisPlatform(User user) {
        final String userID = user.getUserID();
        final Set<ServerSession> serverSessions = userIdSessionMap.get(userID);
        if (serverSessions == null || serverSessions.size() == 0) {
            return false;
        }
        final String userPlatform = user.getPlatform();
        return serverSessions.stream().anyMatch(session -> session.getUser().getPlatform().equals(userPlatform));
    }

    public void closeAll(){
        sessionIdMap.values().forEach(ServerSession::close);
    }
}
