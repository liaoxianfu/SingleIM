package com.liao.im.common.exception;

import java.io.IOException;

/**
 * 数据长度异常
 */
public class DataLengthException extends IOException {
    public DataLengthException(String msg) {
        super(msg);
    }
}
