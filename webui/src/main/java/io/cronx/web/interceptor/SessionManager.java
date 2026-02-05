package io.cronx.web.interceptor;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import com.auth0.jwt.interfaces.DecodedJWT;

import io.cronx.web.constant.ControllerUrlPrefix;
import io.cronx.web.interceptor.model.CheckResult;
import io.cronx.web.mapper.OptUserMapper;
import io.cronx.web.model.entity.OptUserDO;
import io.cronx.web.service.security.JwtService;
import io.cronx.web.webconfig.ConsoleConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionManager implements HandlerInterceptor {

    @Resource
    private JwtService              jwtService;

    @Resource
    private OptUserMapper           optUserMapper;

    @Resource
    private ConsoleConfig           consoleConfig;

    /**
     * maybe multi console need it to verify 
     **/
    public final static String      CSRF_TOKEN_NAME = "csrf-token";

    public final static Set<String> ignoreVerifyUrl = new HashSet<>();

    static {
        ignoreVerifyUrl.add("/login");
        ignoreVerifyUrl.add("/healthcheck");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof ResourceHttpRequestHandler) {
            return true;
        }

        CheckResult checkResult = preHandleInner(request);

        if (checkResult.isSuccess()) {
            return true;
        }

        response.setStatus(checkResult.getErrorCode());
        try (PrintWriter writer = response.getWriter()) {
            writer.write("{}");
            writer.flush();
        }

        return false;
    }

    private boolean ignoreVerify(HttpServletRequest request) {
        String uri = request.getRequestURI();
        for (String ignore : ignoreVerifyUrl) {
            if (uri.endsWith(ignore)) {
                return true;
            }
        }
        return false;
    }

    public CheckResult preHandleInner(HttpServletRequest request) {
        String uri = request.getRequestURI();

        if (!consoleConfig.getEnableSecurity()) {
            return responseOk();
        }

        if (ignoreVerify(request)) {
            return responseOk();
        }

        if (!uri.startsWith(ControllerUrlPrefix.CONSOLE_PREFIX)) {
            return responseOk();
        }

        // validate is login
        DecodedJWT jwt = jwtService.verify(request);
        if (jwt == null) {
            return responseNotLogin("NotLogin.");
        }

        String userName = jwt.getId();
        if (StringUtils.isBlank(userName)) {
            String errorMessage = "Login success, but username is empty. Maybe system is being hacked.";
            log.error(errorMessage);
            return responseSystemError(errorMessage);
        }

        OptUserDO optUserDO = optUserMapper.queryByAccountName(userName);
        if (optUserDO == null) {
            String errorMessage = "User (" + userName + ") not exist.";
            log.error(errorMessage);
            return responseSystemError(errorMessage);
        }

        return responseOk();
    }

    private CheckResult responseOk() {
        return CheckResult.builder()//
            .success(true)
            .message("ok.")
            .errorCode(200)
            .build();
    }

    private CheckResult responseNotLogin(String message) {
        return CheckResult.builder()//
            .success(false)
            .message(message)
            .errorCode(401)
            .build();
    }

    private CheckResult responseNoPageAuthority(String message) {
        return CheckResult.builder()//
            .success(false)
            .message(message)
            .errorCode(406)
            .build();
    }

    private CheckResult responseSystemError(String message) {
        return CheckResult.builder()//
            .success(false)
            .message(message)
            .errorCode(500)
            .build();
    }
}
