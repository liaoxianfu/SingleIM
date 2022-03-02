package com.liao.im.client.handler;

import com.google.protobuf.ByteString;
import com.liao.im.client.controller.ClientSession;
import com.liao.im.common.proto.MsgProto;
import com.liao.im.common.proto.MsgProto.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liao
 * create at 2022:02:28  11:23
 */
@ChannelHandler.Sharable
@Slf4j
public class EchoHandler extends SimpleChannelInboundHandler<Object> {
    public final static EchoHandler INSTANCE = new EchoHandler();

    private EchoHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        final Message data = (Message) msg;
        System.out.println(data.getType());
        if (data.getType().equals(MsgProto.HeadType.LOGIN_RESPONSE)) {
            final MsgProto.LoginResponse loginResponse = data.getLoginResponse();
            log.info("读取的数据为{},{}", loginResponse.getResult(), loginResponse.getdebug());
            if (loginResponse.getResult()) {
                ClientSession.loginSuccess(ctx, data);
            }
        }
        if (data.getType().equals(MsgProto.HeadType.MESSAGE_REQUEST)) {
            final String from = data.getMessageRequest().getFrom();
            final ByteString content = data.getMessageRequest().getContent();
            System.out.printf("[%s] : %s\n", from, content.toStringUtf8());
        }
//        log.info("收到的数据 类型为{}", msg.getClass());
    }


}
