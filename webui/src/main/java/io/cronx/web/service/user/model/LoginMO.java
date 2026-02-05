package io.cronx.web.service.user.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Data
@Builder
public class LoginMO {

    @Tolerate
    public LoginMO(){
    }

    private boolean success;

    private boolean needMore;

    private String  errMsg;

    private String  username;

    private String  token;

}
