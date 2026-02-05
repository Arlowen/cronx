package io.cronx.web.model.base;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseData<T> implements Serializable {

    private String code;

    private String msg;

    private T      data;

    public ResponseData(String code, String msg, T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseData(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public ResponseData(ResultEnum resultEnum){
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
    }

    public ResponseData(ResultEnum resultEnum, T data){
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
        this.data = data;
    }

    public ResponseData(){
    }

    public boolean isSuccess() { return ResultEnum.SUCCESS.getCode().equals(code); }

    public boolean isFail() { return !ResultEnum.SUCCESS.getCode().equals(code); }

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }

    public String getMsg() { return msg; }

    public void setMsg(String msg) { this.msg = msg; }

    public T getData() { return data; }

    public void setData(T data) { this.data = data; }

}
