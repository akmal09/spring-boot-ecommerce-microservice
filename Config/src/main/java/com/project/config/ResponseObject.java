package com.project.config;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseObject implements Serializable {
    private String status;
//    private
    private Object data;
}
