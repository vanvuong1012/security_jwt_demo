package com.example.security_demo.response;

import lombok.Data;

@Data
public class MainResponse<T>{
    private int code;
    private String message;
    private T data;

    public MainResponse(int code , String message){
        this.code = code;
        this.message = message;
    }

    public MainResponse(T data){
        this.data = data;
    }

    public MainResponse(){
        this.code = 1;
        this.message = "SUCCESS";
    }
}