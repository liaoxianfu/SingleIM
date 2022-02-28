package com.liao.im.common.exception;

/**
 * @author liao
 * create at 2021:11:30  23:17
 */
public class PortOutOfRangeException extends Exception{
    public PortOutOfRangeException(){
        super("port 范围为 1-65535");
    }
}
