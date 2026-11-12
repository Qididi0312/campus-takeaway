package com.zhishan.zhishantakeout.common;

import lombok.Data;

/**
 * 统一接口响应结构
 * 所有后端接口都返回这个格式，方便前端统一处理
 *
 * @param <T> data 字段的具体类型，比如 Result<String>、Result<List<Dish>>
 */
@Data
public class Result<T> {
    /** 业务状态码：0 表示成功，其他表示失败 */
    private Integer code;
    /** 提示信息，成功时一般是 "ok"，失败时是错误描述 */
    private String message;
    /** 真正的业务数据，类型由泛型 T 决定 */
    private T data;

    /** 成功响应，附带数据 */
    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(0);
        r.setMessage("ok");
        r.setData(data);
        return r;
    }

    /** 成功响应，不带数据 */
    public static <T> Result<T> success() {
        return success(null);
    }

    /** 失败响应，使用默认 code=1 */
    public static <T> Result<T> error(String message) {
        Result<T> r = new Result<>();
        r.setCode(1);
        r.setMessage(message);
        return r;
    }

    /** 失败响应，自定义 code */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }
}