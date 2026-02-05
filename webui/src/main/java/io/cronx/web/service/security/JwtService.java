package io.cronx.web.service.security;

import javax.servlet.http.HttpServletRequest;

import com.auth0.jwt.interfaces.DecodedJWT;

import io.cronx.web.model.entity.OptUserDO;

public interface JwtService {

    String jwtTokenName = "jwt_token";

    DecodedJWT verify(HttpServletRequest request);

    DecodedJWT verifyJwtToken(String jwtToken);

    String genJwtToken(OptUserDO user);

}
