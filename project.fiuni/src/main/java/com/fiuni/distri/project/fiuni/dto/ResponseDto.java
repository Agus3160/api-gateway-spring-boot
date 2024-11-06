package com.fiuni.distri.project.fiuni.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class ResponseDto<T> implements Serializable {

    int httpStatus;
    boolean success = false;
    String message= null;
    String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
    T data = null;

    public ResponseDto(int httpStatus, boolean success, String message, T data){
        this.data = data;
        this.success = success;
        this.message = message;
        this.httpStatus = httpStatus;
    }

}
