package io.cronx.web.model.fo.user;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginFO {

    @NotNull(message = "{notnull.account}")
    private String account;

    private String password;

}
