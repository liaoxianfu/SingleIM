package com.liao.im.server;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

/**
 * @author liao
 * create at 2022:02:28  17:41
 */
@SpringBootTest
@Slf4j
public class IMServerApplicationTest {
    @Test
    public void test01() throws JsonProcessingException {
        var map = new HashMap<String, Object>(2);
        map.put("userId","1000");
        map.put("password","123456");
        final ObjectMapper objectMapper = new ObjectMapper();
        final String s = objectMapper.writeValueAsString(map);
        System.out.println(s);
        final HttpResponse response = HttpRequest.post("http://127.0.0.1:8080/login").contentType("application/json").body(s).execute();
        System.out.println(response.body());
    }
}
