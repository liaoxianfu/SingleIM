package com.liao.im.common.exception;

import java.io.IOException;

/**
 * @Author: liao
 * @Date 2021:10:27  12:04
 * @Version V1.0
 * @Description:
 */
public class DataLengthException extends IOException {
    public DataLengthException(String msg) {
        super(msg);
    }
}
