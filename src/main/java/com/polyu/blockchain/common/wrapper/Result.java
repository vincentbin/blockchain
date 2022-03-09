package com.polyu.blockchain.common.wrapper;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class Result<T> {

    private String msg;
    private T data;

    /**
     *
     * @param data data
     * @param <T> data clazz
     * @return
     */
    public static <T> Result<T> success(T data){
        return new Result<T>(data);
    }

    /**
     * fail
     * @param codeMsg msg
     * @param <T> data clazz
     * @return
     */
    public static <T> Result<T> error(String codeMsg){
        return new Result<T>(codeMsg);
    }

    private Result(T data) {
        this.data = data;
    }

    private Result(String msg) {
        this.msg = msg;
    }

}