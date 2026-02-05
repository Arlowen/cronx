package io.cronx.web.model.base;

/**
 * The enum Result enum.
 */
public enum ResultEnum {

    /**
     * Success result enum.
     */
    SUCCESS("1", "Request success"),
    /**
     * Error result enum.
     */
    ERROR("0", "Request failure");

    private String code;
    private String msg;

    ResultEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    /**
     * Gets code.
     *
     * @return the code
     */
    public String getCode() { return code; }

    /**
     * Sets code.
     *
     * @param code the code
     */
    public void setCode(String code) { this.code = code; }

    /**
     * Gets msg.
     *
     * @return the msg
     */
    public String getMsg() { return msg; }

    /**
     * Sets msg.
     *
     * @param msg the msg
     */
    public void setMsg(String msg) { this.msg = msg; }
}
