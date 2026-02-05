package io.cronx.web.service.user.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Data
@Builder
public class PasswordInfo {

    @Tolerate
    public PasswordInfo(){
    }

    private String plainPassword;

    private String encryptPassword;

    private String key;

    private String salt;
}
