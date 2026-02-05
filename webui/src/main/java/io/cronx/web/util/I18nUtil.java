package io.cronx.web.util;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;

import io.cronx.toolkit.utils.StringUtils;
import io.cronx.toolkit.utils.i18n.I18nUtils;
import io.cronx.web.component.i18n.I18nAuthMsgKeys;
import io.cronx.web.exception.ConsoleErrorCode;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class I18nUtil {

    private static final I18nUtils I18N_UTILS    = I18nUtils.initI18n();
    @Setter
    private static String          defaultLocale = "zh_CN";

    static {
        I18N_UTILS.setName("CRONX");
        I18N_UTILS.setDefaultI18nKey(defaultLocale);
        LocaleContextHolder.setLocale(I18nUtils.getLocale(defaultLocale));

        // loadResources
        I18N_UTILS.loadResources("/i18n/msg");
        I18N_UTILS.loadResources("/i18n/validation");
        I18N_UTILS.loadResources(I18nAuthMsgKeys.class);
        I18N_UTILS.loadResources(ConsoleErrorCode.class);

        // check loadResources
        I18N_UTILS.checkDifferenceOnWarn("zh_CN");
        I18N_UTILS.checkDifferenceOnWarn("en_US");
    }

    public static Locale getLocale() {
        LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
        Locale locale;

        if (localeContext != null) {
            locale = localeContext.getLocale();
        } else if (StringUtils.isNotBlank(defaultLocale)) {
            locale = I18nUtils.getLocale(defaultLocale);
        } else {
            locale = Locale.getDefault();
        }

        return locale;
    }

    public static String getMessage(ConsoleErrorCode errorCode) {
        return getMessage(errorCode.name(), getLocale());
    }

    public static String getMessage(ConsoleErrorCode errorCode, Object... args) {
        return getMessage(errorCode.name(), getLocale(), args);
    }

    public static String getMessage(ConsoleErrorCode errorCode, Locale locale) {
        return getMessage(errorCode.name(), locale);
    }

    public static String getMessage(ConsoleErrorCode errorCode, Locale locale, Object... args) {
        return getMessage(errorCode.name(), locale, args);
    }

    public static String getMessage(String key) {
        return getMessage(key, getLocale());
    }

    public static String getMessage(String key, Object... args) {
        return getMessage(key, getLocale(), args);
    }

    public static String getMessage(String key, Locale locale, Object... args) {
        return I18N_UTILS.getMessage(key, args, locale);
    }
}
