package com.liao.im.common.utils;

import cn.hutool.core.util.ObjectUtil;
import com.liao.im.common.config.IMConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 定义proto 编码器
 * @author liao
 * create at 2022:02:28  16:11
 */
@Slf4j
public class ProtoEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf byteBuf) throws Exception {
        if (ObjectUtil.isNotNull(msg)) {
            byte[] data = DataPackUtil.msg2BytesWithMagicNumber(msg, IMConfig.MAGIC_NUMBER);
            log.debug("写入数据 {}",data.length);
            byteBuf.writeBytes(data);
        } else {
            throw new Exception("data is null");
        }
    }
}
