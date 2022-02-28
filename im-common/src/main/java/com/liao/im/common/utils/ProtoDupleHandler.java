package com.liao.im.common.utils;

import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * 编码器和解码器结合
 *
 * @author liao
 * create at 2022:02:28  16:44
 */
public class ProtoDupleHandler extends CombinedChannelDuplexHandler<ProtoDecoder, ProtoEncoder> {
    public ProtoDupleHandler() {
        super(new ProtoDecoder(), new ProtoEncoder());
    }
}
