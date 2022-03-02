package com.liao.im.common.proto;

import com.liao.im.common.proto.MsgProto.LoginRequest;
import com.liao.im.common.proto.MsgProto.LoginResponse;
import com.liao.im.common.proto.MsgProto.Message;
import com.liao.im.common.proto.MsgProto.MessageRequest;

/**
 * @author liao
 * create at 2022:03:02  15:31
 */
public class MsgBuilder {

    public static Message loginRequestMessageBuild(LoginRequest loginRequest, long sequence) {
        return Message.newBuilder().setType(MsgProto.HeadType.LOGIN_REQUEST)
                .setSequence(sequence)
                .setLoginRequest(loginRequest)
                .build();
    }


    public static Message loginResponseMessageBuild(LoginResponse response, String sessionId, long sequence) {
        return Message.newBuilder()
                .setType(MsgProto.HeadType.LOGIN_RESPONSE)
                .setSequence(sequence)
                .setSessionId(sessionId)
                .setLoginResponse(response)
                .build();
    }


    public static Message messageRequestMessageBuild(MessageRequest messageRequest, String sessionId, long sequence) {
        return Message.newBuilder().setType(MsgProto.HeadType.MESSAGE_REQUEST)
                .setSequence(sequence)
                .setMessageRequest(messageRequest)
                .setSessionId(sessionId).build();
    }

    public static Message heartbeatMessageBuild( long sequence) {
        return Message.newBuilder().setType(MsgProto.HeadType.KEEPALIVE_REQUEST)
                .setSequence(sequence)
                .build();
    }


}

