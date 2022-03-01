package com.liao.im.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author liao
 * create at 2022:02:28  21:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private String userID;
    private String nickName;
    private String platform;
}
