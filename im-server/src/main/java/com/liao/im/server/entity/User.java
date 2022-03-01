package com.liao.im.server.entity;

import lombok.Data;

/**
 * @author liao
 * create at 2022:03:01  16:39
 */
@Data
public class User {
    private String uid;
    private String platform;
    private String password;
}
