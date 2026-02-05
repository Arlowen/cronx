package io.cronx.web.interceptor.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckResult {

    private boolean success;
    private String  message;
    private int     errorCode;

}
