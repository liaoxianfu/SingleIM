package com.liao.im.common.utils;

import cn.hutool.core.util.ObjectUtil;
import com.liao.im.common.config.IMConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 定义proto 解码器
 *
 * @author liao
 * create at 2022:02:28  16:12
 */
@Slf4j
public class ProtoDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex(); // 标记位置
        if (in.readableBytes() < 8) {
            log.debug("位数不足");
            return;
        }
        final int count = in.readInt();
        if (count < 0) {
            log.error("数据出现错误,关闭连接");
            ctx.close();
            return;
        }

        final int magicNumber = in.readInt();
        log.debug("读取的magicNumber= {}", magicNumber);
        if (magicNumber != IMConfig.MAGIC_NUMBER) {
            log.error("校验失败 读取的魔数为{}", magicNumber);
            ctx.close();
            return;
        }

        if (in.readableBytes() < count) {
            log.info("数据不足，等待下次");
            in.resetReaderIndex(); // 重置位置
            return;
        }
        final byte[] bs;
        if (in.hasArray()) {
            log.debug("堆内存");
            // 进行slice 浅拷贝
            final ByteBuf byteBuf = in.slice();
            bs = byteBuf.array();
        } else {
            log.debug("直接内存");
            bs = new byte[count];
            in.readBytes(bs);
        }
        final Object o = ObjectUtil.deserialize(bs);
        log.debug("对象解析成功 加入列表");
        out.add(o);
    }

}
