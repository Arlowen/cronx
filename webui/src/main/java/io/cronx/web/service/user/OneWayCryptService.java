package io.cronx.web.service.user;

import io.cronx.web.service.user.model.PasswordInfo;

public interface OneWayCryptService {

    /**
     * encrypt password one way
     */
    PasswordInfo encrypt(String plainPassword);

    /**
     * whether a plaintext password matches one that has been hashed previously
     */
    boolean isMatch(PasswordInfo passwordInfo);
}
