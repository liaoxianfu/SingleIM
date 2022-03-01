package com.liao.im.server.web.controller;

import com.liao.im.common.config.IMConfig;
import com.liao.im.server.web.config.JsonBody;
import com.liao.im.server.web.entity.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author liao
 * create at 2022:02:28  17:23
 */
@RestController
@Slf4j
public class LoginController {
    @PostMapping("/login")
    public JsonBody login(@RequestBody @Valid UserVo user, BindingResult result) {
        if (result.hasErrors()) {
            final JsonBody errJson = JsonBody.error();
            result.getFieldErrors().forEach(err -> {
                errJson.data(err.getField(), err.getDefaultMessage()).code(IMConfig.LOGIN_FIELD);
            });
            return errJson;
        }
        log.debug("user is {}", user);
        if (user.getUserId().equals("1000") && user.getPassword().equals("123456")) {
            return JsonBody.success().code(IMConfig.LOGIN_SUCCESS).data("nickName", "test001").data("token", "aaaaaaaaa");
        }
        return JsonBody.error().code(IMConfig.LOGIN_FIELD).data("info", "未知错误");
    }
}
