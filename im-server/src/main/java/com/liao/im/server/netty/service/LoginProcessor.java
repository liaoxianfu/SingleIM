package com.liao.im.server.netty.service;

import com.google.protobuf.ByteString;
import com.liao.im.common.config.IMConfig;
import com.liao.im.common.entity.User;
import com.liao.im.common.proto.MsgBuilder;
import com.liao.im.common.proto.MsgProto;
import com.liao.im.common.proto.MsgProto.LoginResponse;
import com.liao.im.server.session.ServerSession;
import com.liao.im.server.session.SessionMap;
import com.liao.im.server.web.entity.vo.UserVo;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * @author liao
 * create at 2022:03:01  16:25
 */
@Service
@Slf4j
public class LoginProcessor implements Processor {
    public Boolean process(ChannelHandlerContext ctx, MsgProto.Message message) {
        final ServerSession session = new ServerSession(ctx.channel());
        final MsgProto.LoginRequest loginRequest = message.getLoginRequest();
        final int platform = loginRequest.getPlatform();
        final String uid = loginRequest.getUid();
        final String token = loginRequest.getToken();
        final long sequence = message.getSequence();
        final User user = new User();
        user.setUserID(uid);
        user.setPlatform(IMConfig.platform(platform));
        final UserVo userVo = new UserVo();
        userVo.setUserId(uid);
        userVo.setPassword(token);
        if (!checkUser(userVo)) {
            return errMsg(session, sequence);
        }
        final boolean b = SessionMap.INSTANCE.hasLoginInThisPlatform(user);
        if (b) {
            log.error("该平台用户已经登录了");
            // TODO 将已经登录的用户踢出
            return errMsg(session, sequence);
        }
        session.setUser(user);
        session.bind();
        return successMsg(session, sequence);
    }

    private boolean errMsg(ServerSession session, long sequence) {
        log.info("用户登录失败");
        final LoginResponse loginResponse = LoginResponse.newBuilder().setCode(4000).
                setResult(false)
                .setdebugBytes(ByteString.copyFrom("登录失败".getBytes(StandardCharsets.UTF_8)))
                .build();
        var msg = MsgBuilder.loginResponseMessageBuild(loginResponse, session.getSessionId(), sequence);
        session.writeAndFlush(msg);
        return false;
    }


    private boolean successMsg(ServerSession session, long sequence) {
        log.info("用户登录成功");
        final LoginResponse loginResponse = LoginResponse.newBuilder().setCode(2000).
                setResult(true)
                .setdebugBytes(ByteString.copyFrom("登录成功".getBytes(StandardCharsets.UTF_8)))
                .build();
        var msg = MsgBuilder.loginResponseMessageBuild(loginResponse, session.getSessionId(), sequence);
        session.writeAndFlush(msg);
        return true;
    }

    private Boolean checkUser(UserVo userVo) {
        // TODO 调用http进行验证
        return true;
    }
}
