package io.cronx.web.model.vo.user;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Data
@Builder
public class LoginDataVO {

    @Tolerate
    public LoginDataVO(){
    }

    private long    lastTryLoginTimeMs = 0;

    private int     loginFailCount     = 0;

    private boolean locked             = false;

    private String  loginFailMsg;
}
