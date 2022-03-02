package com.example.security_demo.response;

import lombok.Data;
import org.springframework.http.HttpStatus;
@Data
public class ApiException extends RuntimeException{
    private int code;
    private HttpStatus httpStatus;

    public ApiException(ERROR exception) {
        super(exception.getMessage());
        this.code = exception.getCode();

        this.httpStatus = HttpStatus.OK;
    }

    public ApiException(ERROR exception, HttpStatus status) {
        super();
        this.code = exception.getCode();

        this.httpStatus = status;
    }


    public ApiException(int code, String errorMsg) {
        super(errorMsg);
        this.code = code;
        this.httpStatus = HttpStatus.OK;
    }

    public ApiException(ERROR exception, String errorMsg) {
        super(errorMsg);
        this.code = exception.getCode();

        this.httpStatus = HttpStatus.OK;
    }

    public ApiException(int code, String errorMsg, HttpStatus httpStatus) {
        super(errorMsg);
        this.code = code;

        this.httpStatus = httpStatus;
    }
}
