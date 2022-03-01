package com.liao.im.server.session;

import com.liao.im.common.entity.User;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liao
 * create at 2022:03:01  11:54
 */
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
        if (userIdSessionMap.containsKey(userID)) {
            userIdSessionMap.get(userID).add(session);
        } else {
            final HashSet<ServerSession> sessions = new HashSet<>();
            sessions.add(session);
            userIdSessionMap.put(userID, sessions);
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
        final String userID = session.getUser().getUserID();
        userIdSessionMap.get(userID).remove(session);
        sessionIdMap.remove(sessionId);
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

}
