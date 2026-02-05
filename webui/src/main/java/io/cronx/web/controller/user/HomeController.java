package io.cronx.web.controller.user;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.cronx.web.constant.ControllerUrlPrefix;
import io.cronx.web.model.base.ResponseData;
import io.cronx.web.model.base.ResponseDataUtil;
import io.cronx.web.model.fo.user.LoginFO;
import io.cronx.web.service.security.JwtService;
import io.cronx.web.service.user.OptUserService;
import io.cronx.web.service.user.model.LoginMO;

// reference https://stackoverflow.com/questions/31846893/springboot-template-files-are-not-loaded
@RestController
@RequestMapping(ControllerUrlPrefix.CONSOLE_PREFIX + "/")
public class HomeController {

    @Resource
    private OptUserService optUserService;

    @RequestMapping(value = "/healthcheck", method = RequestMethod.POST)
    public String healthCheck() {
        return "ok";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseData<?> login(@Valid @RequestBody LoginFO loginFO, HttpServletResponse response) {
        LoginMO loginMO = optUserService.login(loginFO);
        if (loginMO.isSuccess()) {
            Cookie cookie = new Cookie(JwtService.jwtTokenName, loginMO.getToken());
            // let fronted to extract jwt token as csrf token
            cookie.setHttpOnly(false);
            cookie.setMaxAge((int) (OptUserService.LOGIN_EXPIRE_TIME_MS / 1000));
            response.addCookie(cookie);
            return ResponseDataUtil.buildSuccess(loginMO);
        } else {
            return ResponseDataUtil.buildError(loginMO.getErrMsg());
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseData<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt_token", StringUtils.EMPTY);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseDataUtil.buildSuccess();
    }
}
