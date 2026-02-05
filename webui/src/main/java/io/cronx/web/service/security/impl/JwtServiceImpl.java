package io.cronx.web.service.security.impl;

import static io.cronx.web.service.user.OptUserService.LOGIN_EXPIRE_TIME_MS;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import io.cronx.web.model.entity.OptUserDO;
import io.cronx.web.service.security.JwtService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService, InitializingBean {

    /**
     * default use hmacsha256
     */
    @Value("${jwt.secret}")
    private String       secret;

    private Algorithm    algorithm;

    private final String issuer = "CronX";

    @Override
    public void afterPropertiesSet() {
        try {
            algorithm = Algorithm.HMAC256(secret);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("generate sign algorithm error.msg:" + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    @Override
    public DecodedJWT verify(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtTokenName);
        String headerValue = request.getHeader(jwtTokenName);
        String requestParameterValue = request.getParameter(jwtTokenName);
        String jwtToken;
        if (cookie != null) {
            jwtToken = cookie.getValue();
        } else if (headerValue != null) {
            jwtToken = headerValue;
        } else if (requestParameterValue != null) {
            jwtToken = requestParameterValue;
        } else {
            return null;
        }

        return verifyJwtToken(jwtToken);
    }

    @Override
    public DecodedJWT verifyJwtToken(String jwtToken) {
        if (StringUtils.isBlank(jwtToken)) {
            throw new IllegalArgumentException("jwt token can not be empty.");
        }

        JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build();
        try {
            return verifier.verify(jwtToken);
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    @Override
    public String genJwtToken(OptUserDO user) {
        // token expire time
        long nowMills = System.currentTimeMillis();
        Date issueAt = new Date(nowMills);
        Date expireAt = new Date(nowMills + LOGIN_EXPIRE_TIME_MS);

        // username used for django-jwt
        return JWT.create().withIssuer(issuer).withIssuedAt(issueAt).withExpiresAt(expireAt).withJWTId(user.getUsername()).sign(algorithm);
    }

}
