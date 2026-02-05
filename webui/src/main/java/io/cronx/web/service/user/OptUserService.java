package io.cronx.web.service.user;

import org.springframework.stereotype.Service;

import io.cronx.web.model.fo.user.LoginFO;
import io.cronx.web.service.user.model.LoginMO;

@Service
public interface OptUserService {

    String UID                  = "uid";

    long   LOGIN_EXPIRE_TIME_MS = 24 * 3600 * 1000;

    LoginMO login(LoginFO loginFO);

}
