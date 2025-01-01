package com.huex.bamboohub.controller;

public class Response<T> {
    private T data;
    private Boolean success;
    private String errorMsg;

    public static <K> Response<K> newSuccess(K data) {
        Response<K> response=new Response<>();
        response.setData(data);
        response.setSuccess(true);
        return response;
    }

    public static <K> Response<K> newFail(String errorMsg) {
        Response<K> response=new Response<>();
        response.setSuccess(false);
        response.setErrorMsg(errorMsg);
        return response;
    }

    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
    public Boolean isSuccess() {
        return success;
    }
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    public String getErrorMsg() {
        return errorMsg;
    }
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
