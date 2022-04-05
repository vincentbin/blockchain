package com.polyu.blockchain.common.wrapper;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class HTTPResult<T> {

    private String msg;
    private T data;

    /**
     * success return & set data
     * @param data data
     * @param <T> data clazz
     * @return
     */
    public static <T> HTTPResult<T> success(T data) {
        return new HTTPResult<T>(data);
    }

    /**
     * success return & set msg
     * @param data data
     * @param msg message
     * @param <T> data clazz
     * @return Result
     */
    public static <T> HTTPResult<T> success(T data, String msg) {
        HTTPResult<T> res = new HTTPResult<>(data);
        res.setMsg(msg);
        return res;
    }

    /**
     * fail
     * @param codeMsg msg
     * @param <T> data clazz
     * @return
     */
    public static <T> HTTPResult<T> error(String codeMsg) {
        return new HTTPResult<T>(codeMsg);
    }

    private HTTPResult(T data) {
        this.data = data;
    }

    private HTTPResult(String msg) {
        this.msg = msg;
    }

}