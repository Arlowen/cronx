package io.cronx.web.exception;

import io.cronx.toolkit.utils.i18n.I18nResource;
import io.cronx.web.util.I18nUtil;

@I18nResource
public enum ConsoleErrorCode implements BaseErrorCode {

    SYSTEM_ERROR(ErrorType.SYSTEM, "1001"),
    METHOD_ARGUMENT_INVALID_ERROR(ErrorType.SYSTEM, "1002");

    private final String    code;
    private final ErrorType type;

    ConsoleErrorCode(ErrorType type, String code){
        this.code = code;
        this.type = type;
    }

    public String getName() { return this.name(); }

    public String getCode() { return code; }

    public String getType() { return type.name(); }

    public String getMessage(String... params) {
        return I18nUtil.getMessage(this, params);
    }

}
