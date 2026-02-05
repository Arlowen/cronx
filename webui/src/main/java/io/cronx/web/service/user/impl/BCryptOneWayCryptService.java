package io.cronx.web.service.user.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import io.cronx.web.service.user.OneWayCryptService;
import io.cronx.web.service.user.model.PasswordInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BCryptOneWayCryptService implements OneWayCryptService {

    private final int                 LOG_ROUNDS         = 10;

    private static OneWayCryptService oneWayCryptService = new BCryptOneWayCryptService();

    private BCryptOneWayCryptService(){
    }

    public static OneWayCryptService getInstance() { return oneWayCryptService; }

    @Override
    public PasswordInfo encrypt(String plainPassword) {
        if (StringUtils.isNotBlank(plainPassword)) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(LOG_ROUNDS);
            String encodedPwd = encoder.encode(plainPassword);
            return PasswordInfo.builder().encryptPassword(encodedPwd).build();
        } else {
            throw new RuntimeException("Plain password can not be empty when use one-way encrypt");
        }
    }

    @Override
    public boolean isMatch(PasswordInfo passwordInfo) {
        if (StringUtils.isNotBlank(passwordInfo.getEncryptPassword()) && StringUtils.isNotBlank(passwordInfo.getPlainPassword())) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(LOG_ROUNDS);
            return encoder.matches(passwordInfo.getPlainPassword(), passwordInfo.getEncryptPassword());
        } else {
            throw new RuntimeException("Encrypted password and plain password can not be empty when test whether two password is match");
        }
    }
}
