package com.liao.im.common;

import com.liao.im.common.proto.MsgProto;
import org.junit.Test;

/**
 * @author liao
 * create at 2022:02:28  16:01
 */
public class IMCommonTest {
    @Test
    public void test1() {
        final MsgProto.Message.Builder mb = MsgProto.Message.newBuilder();
        var loginRequest = MsgProto.LoginRequest.newBuilder()
                .setPlatform(1).setAppVersion("v1.0").build();
        final MsgProto.Message message = mb.setType(MsgProto.HeadType.LOGIN_REQUEST).setSequence(1)
                .setLoginRequest(loginRequest).build();
        System.out.println(message);
    }

}
