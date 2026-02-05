package io.cronx.web.webconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@Configuration
@FieldNameConstants
public class ConsoleConfig {

    @Value("${spring.mail.host}")
    private String  emailHostConfigKey;

    @Value("${spring.mail.port}")
    private String  emailPortConfigKey;

    @Value("${spring.mail.username}")
    private String  emailUserNameConfigKey;

    @Value("${spring.mail.password}")
    private String  emailPasswordConfigKey;

    @Value("${spring.mail.properties.from}")
    private String  emailFromConfigKey;

    @Value("${cronx.enable.security:true}")
    private Boolean enableSecurity;

    @Value("${cronx.login.retry.max-count:3}")
    private String  retryLoginMaxCount;

    @Value("${cronx.login.reset.period.minuetes:1}")
    private String  resetLoginLimitationWaitTimeMin;

    @Value("${cronx.config.i18n.default_locale:zh_CN}")
    private String  i18nDefaultLocale;

}
