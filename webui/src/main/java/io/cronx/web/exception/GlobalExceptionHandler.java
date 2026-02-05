package io.cronx.web.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import io.cronx.toolkit.utils.JacksonUtils;
import io.cronx.web.model.base.ResponseData;
import io.cronx.web.model.base.ResponseDataUtil;
import io.cronx.web.model.exception.ConsoleRuntimeException;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseData handleNormalException(HttpServletRequest request, Exception e) {
        List<String> errorMsgs = new ArrayList<>();
        errorMsgs.add(e.getMessage());
        String msgs = JacksonUtils.toJsonString(errorMsgs);

        return ResponseDataUtil.buildError(ConsoleErrorCode.SYSTEM_ERROR.getCode(), msgs);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseData handleMethodArgumentException(HttpServletRequest request, MethodArgumentNotValidException e) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        List<String> errorMsgs = errors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        String msgs = JacksonUtils.toJsonString(errorMsgs);

        return ResponseDataUtil.buildError(ConsoleErrorCode.METHOD_ARGUMENT_INVALID_ERROR.getCode(), msgs);
    }

    @ExceptionHandler(value = ConsoleRuntimeException.class)
    @ResponseBody
    public ResponseData handleNormalException(HttpServletRequest request, ConsoleRuntimeException e) {
        List<String> errorMsgs = new ArrayList<>();
        errorMsgs.add(e.getMessage());
        String msgs = JacksonUtils.toJsonString(errorMsgs);

        return ResponseDataUtil.buildError(e.getErrorCode().getCode(), msgs);
    }
}
