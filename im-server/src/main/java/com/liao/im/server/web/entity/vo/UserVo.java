package com.liao.im.server.web.entity.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author liao
 * create at 2022:02:28  17:28
 */

@Data
public class UserVo implements Serializable {
    @NotNull
    private String userId;
    @NotNull
    @Length(max = 15, min = 6)
    private String password;
}
