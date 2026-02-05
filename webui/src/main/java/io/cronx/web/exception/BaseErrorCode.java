package io.cronx.web.exception;

public interface BaseErrorCode {

    String getName();

    String getCode();

    String getType();

    String getMessage(String... params);
}
