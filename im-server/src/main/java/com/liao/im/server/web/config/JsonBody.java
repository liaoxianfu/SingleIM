package com.liao.im.server.web.config;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * @author liao
 */
@Data
public class JsonBody implements Serializable {
    private JsonBody() {
    }

    private int code;
    private String message;
    private Map<Object, Object> data = new HashMap<>();

    public static JsonBody success() {
        JsonBody r = new JsonBody();
        r.setCode(20000);
        r.setMessage("成功");
        return r;
    }

    public void setData(Object value) {
        data.put("data", value);
    }

    public void setData(String key, Object value) {
        data.put(key, value);
    }

    public JsonBody data(Object data) {
        this.setData(data);
        return this;
    }

    public static JsonBody error() {
        JsonBody r = new JsonBody();
        r.setCode(50000);
        r.setMessage("失败");
        return r;
    }

    public JsonBody code(int code) {
        this.code = code;
        return this;
    }

    public JsonBody data(String key, String value) {
        setData(key, value);
        return this;
    }

    public JsonBody message(String message) {
        this.message = message;
        return this;
    }


}
