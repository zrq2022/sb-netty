package cn.aoe.sb.netty.common.base;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhaoruiqing
 * @version 1.0
 * @since 2022/8/23
 */
@Slf4j
public class Response<T> {
    private int code;
    private String message;
    private T data;


    public Response(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public static <T> Response<T> ok(T data) {
        return new Response<>(200, "ok", data);
    }

    public static Response<Void> ok() {
        return new Response(200, "ok", null);
    }

    public static <T> Response<T> ok(String msg, T data) {
        return new Response<>(200, msg, data);
    }

    public static Response fail(String msg) {
        return new Response<String>(500, msg, null);
    }
}