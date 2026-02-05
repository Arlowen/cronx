package io.cronx.web.model.base;

public class ResponseDataUtil {

    public static <T> ResponseData<T> buildSuccess(T data) {
        return new ResponseData<T>(ResultEnum.SUCCESS, data);
    }

    public static ResponseData<String> buildSuccess() {
        return new ResponseData<>(ResultEnum.SUCCESS);
    }

    public static ResponseData<String> buildSuccess(String code, String msg) {
        return new ResponseData<>(code, msg);
    }

    /**
     * [Attention] Do not use String object data when you are sending request
     */
    public static <T> ResponseData<T> buildSuccess(String code, String msg, T data) {
        return new ResponseData<>(code, msg, data);
    }

    public static ResponseData<String> buildSuccess(ResultEnum resultEnum) {
        return new ResponseData<>(resultEnum);
    }

    public static ResponseData<String> buildError() {
        return new ResponseData<>(ResultEnum.ERROR);
    }

    public static ResponseData<String> buildError(String msg) {
        return new ResponseData<>(ResultEnum.ERROR.getCode(), msg);
    }

    public static ResponseData<String> buildError(String code, String msg) {
        return new ResponseData<>(code, msg);
    }

    public static <T> ResponseData<T> buildError(String code, String msg, T data) {
        return new ResponseData<>(code, msg, data);
    }

    public static ResponseData<String> buildError(ResultEnum resultEnum) {
        return new ResponseData<>(resultEnum);
    }

    public static <T> boolean isSuccess(ResponseData<T> responseData) {
        return responseData != null && responseData.getCode().equals(ResultEnum.SUCCESS.getCode());
    }

    public static <T> boolean isFailed(ResponseData<T> responseData) {
        return responseData == null || !responseData.getCode().equals(ResultEnum.SUCCESS.getCode());
    }

}
