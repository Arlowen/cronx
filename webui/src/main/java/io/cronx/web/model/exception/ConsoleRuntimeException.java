package io.cronx.web.model.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;

import io.cronx.web.exception.ConsoleErrorCode;

public class ConsoleRuntimeException extends RuntimeException {

    private final String[]         params;
    private final ConsoleErrorCode errorCode;

    public ConsoleRuntimeException(ConsoleErrorCode errorCode, String... params){
        super(errorCode.getMessage(params));
        this.params = params;
        this.errorCode = errorCode;
    }

    public ConsoleRuntimeException(ConsoleErrorCode errorCode, Throwable e, String... params){
        super(errorCode.getMessage(params), e);
        this.params = params;
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        if (super.getMessage() != null) {
            return super.getMessage();
        } else {
            Throwable e = ExceptionUtils.getRootCause(this);
            return e.getMessage();
        }
    }

    @Override
    public String toString() {
        return getLocalizedMessage();
    }

    public String[] getParams() { return params; }

    public ConsoleErrorCode getErrorCode() { return errorCode; }
}
