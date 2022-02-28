package com.liao.im.common.utils;

import cn.hutool.core.util.ObjectUtil;
import com.google.gson.GsonBuilder;
import com.liao.im.common.exception.DataLengthException;

import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * 对象与byte数组之间的转换工具类
 *
 * @author liao
 */
public class DataPackUtil {
    private static final int INTEGER_SIZE = 4;
    private static final GsonBuilder GB = new GsonBuilder();

    static {
        GB.disableHtmlEscaping();
    }

    /**
     * 将int数据转换为4字节byte
     */
    public static byte[] int2BytesLe(int num) {
        return new byte[]{
                (byte) (num >> 24 & 0xff),
                (byte) (num >> 16 & 0xff),
                (byte) (num >> 8 & 0xff),
                (byte) (num & 0xff),
        };
    }

    /**
     * 将4字节数组转换为int数据 小端字节序
     */
    public static int bytes2IntLe(byte[] bs) throws DataLengthException {
        if (bs.length != INTEGER_SIZE) {
            throw new DataLengthException("对象数据长度数组不为4");
        }
        return (bs[0] & 0xff) << 24 |
                (bs[1] & 0xff) << 16 |
                (bs[2] & 0xff) << 8 |
                (bs[3] & 0xff);
    }

    /**
     * 合并数组
     */
    private static byte[] mergeBytes(byte[] first, byte[] second) {
        int firstLen = first.length;
        int secondLen = second.length;
        byte[] data = new byte[firstLen + secondLen];
        System.arraycopy(first, 0, data, 0, firstLen);
        System.arraycopy(second, 0, data, firstLen, secondLen);
        return data;
    }

    /**
     * 不定数组拷贝
     *
     * @param bs 不定数组
     * @return 字节数组
     */
    public static byte[] mergeBytes(byte[]... bs) {
        int count = 0;
        for (byte[] b : bs) {
            if (count < Integer.MAX_VALUE) {
                count += b.length;
            } else {
                throw new ArrayIndexOutOfBoundsException("数组太大");
            }
        }
        final byte[] data = new byte[count];
        int start = 0;
        for (byte[] b : bs) {
            System.arraycopy(b, 0, data, start, b.length);
            start += b.length;
        }
        return data;
    }

    /**
     * 将对象转换为字节
     * 转换后字节数组组成为 表示数据长度（int类）的4字节数组|真实数据字节数组
     */
    public static byte[] msg2Bytes(Object msg) throws NotSerializableException {
        if (msg instanceof Serializable) {
            byte[] bs = ObjectUtil.serialize(msg);
            byte[] bsLen = int2BytesLe(bs.length);
            return mergeBytes(bsLen, bs);
        }
        throw new NotSerializableException("数据没有进行序列化");
    }

    /**
     * 携带魔数的对象转换为字节数组
     */
    public static byte[] msg2BytesWithMagicNumber(Object msg, int magicNumber) throws NotSerializableException {
        if (msg instanceof Serializable) {
            byte[] bs = ObjectUtil.serialize(msg);
            final byte[] mn = int2BytesLe(magicNumber);
            final byte[] bsLen = int2BytesLe(bs.length);
            return mergeBytes(bsLen, mn, bs);
        }
        throw new NotSerializableException("数据没有进行序列化");
    }

    /**
     * 将json字符串转换为byte数组
     * 转换后字节数组组成为 表示数据长度（int类）的4字节数组|真实数据字节数组
     */
    public static byte[] json2Bytes(String jsonStr) {
        byte[] bs = jsonStr.getBytes(StandardCharsets.UTF_8);
        byte[] bsLen = int2BytesLe(bs.length);
        return mergeBytes(bsLen, bs);
    }

    /**
     * 将对象转换为json字节数组
     */
    public static byte[] json2Bytes(Object o) {
        String json = GB.create().toJson(o);
        return json2Bytes(json);
    }

    /**
     * 从输入流读取一个完整对象的数据大小
     */
    public static byte[] readBytes(InputStream in) throws IOException {
        byte[] bs = new byte[INTEGER_SIZE];
        int len = in.read(bs);
        while (len < INTEGER_SIZE) {
            len += in.read(bs, len, INTEGER_SIZE - len);
        }
        int dataLen = bytes2IntLe(bs);
        byte[] data = new byte[dataLen];
        len = in.read(data);
        while (len < dataLen) {
            len += in.read(data, len, dataLen - len);
        }
        return data;
    }

    /**
     * 将输入流转换为对象
     */
    public static Object inputStream2Object(InputStream in) throws IOException {
        byte[] data = readBytes(in);
        return ObjectUtil.deserialize(data);
    }

    /**
     * 将字节数组转换为对象
     */
    public static <T> T bytes2Object(byte[] bs, Class<T> clazz) {
        String s = new String(bs, StandardCharsets.UTF_8);
        return GB.create().fromJson(s, clazz);
    }


    /**
     * 将输入流转换为json对象
     */
    public static <T> T inputSteam2JsonObject(InputStream in, Class<T> clazz) throws IOException {
        byte[] data = readBytes(in);
        return bytes2Object(data, clazz);
    }

    /**
     * 将一个byte数组加上长度字节数组放在前面
     */
    public static byte[] bytesAddLen(byte[] bs) {
        int len = bs.length;
        final byte[] lenBytes = int2BytesLe(len);
        return mergeBytes(lenBytes, bs);
    }

}
