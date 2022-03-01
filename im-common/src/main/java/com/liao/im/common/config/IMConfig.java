package com.liao.im.common.config;

/**
 * @author liao
 * create at 2022:02:28  16:32
 */
public class IMConfig {
    // 定义魔数版本
    public static final int MAGIC_NUMBER = 100;

    // 定义登录失败的状态码
    public static final int LOGIN_FIELD = 5000;

    // 定义登录成功的状态码
    public static final int LOGIN_SUCCESS = 2000;


    public static String platform(int code) {
        String res = "";
        switch (code) {
            case 1:
                res = "windows";
                break;
            case 2:
                res = "Android";
                break;
            case 3:
                res = "IOS";
                break;
            default:
                res = "unknown";
        }
        return res;
    }
}
