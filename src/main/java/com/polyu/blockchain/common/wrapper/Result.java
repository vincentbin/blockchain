package com.polyu.blockchain.common.wrapper;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class Result<T> {

    private String msg;
    private T data;

    /**
     * success return & set data
     * @param data data
     * @param <T> data clazz
     * @return
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>(data);
    }

    /**
     * success return & set msg
     * @param data data
     * @param msg message
     * @param <T> data clazz
     * @return Result
     */
    public static <T> Result<T> success(T data, String msg) {
        Result<T> res = new Result<>(data);
        res.setMsg(msg);
        return res;
    }

    /**
     * fail
     * @param codeMsg msg
     * @param <T> data clazz
     * @return
     */
    public static <T> Result<T> error(String codeMsg) {
        return new Result<T>(codeMsg);
    }

    private Result(T data) {
        this.data = data;
    }

    private Result(String msg) {
        this.msg = msg;
    }

}