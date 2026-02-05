package io.cronx.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.cronx.web.component.i18n.ConfigI18nKey;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigDef {

    String name() default "";

    ConfigI18nKey descKey() default ConfigI18nKey.CONFIG_DESCRIPTION_EMPTY;

    String configType() default "";

    String defaultValue() default "";

    String valueRange() default "";

    boolean dynamic() default false;

    boolean readOnly() default true;

    String userRoleType() default "DEFAULT";

    String configTagType() default "NORMAL";

    String taskType() default "";

    //    boolean isSecret() default false;
}
