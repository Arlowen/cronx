package io.cronx.web.component.config;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.cronx.web.annotation.ConfigDef;
import io.cronx.web.component.i18n.ConfigI18nKey;
import io.cronx.web.constant.api.RequestMethod;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@FieldNameConstants
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiSourceConfig {

    @ConfigDef(name = "host", descKey = ConfigI18nKey.CONFIG_HOST_DESCRIPTION)
    private String              host;

    @ConfigDef(name = "requestType", descKey = ConfigI18nKey.CONFIG_REQUEST_TYPE_DESCRIPTION)
    private RequestMethod       requestType;

    @ConfigDef(name = "cookies", descKey = ConfigI18nKey.CONFIG_COOKIES_DESCRIPTION)
    private Map<String, Object> cookies;

    @ConfigDef(name = "params", descKey = ConfigI18nKey.CONFIG_PARAMS_DESCRIPTION)
    private Map<String, Object> params;

    @ConfigDef(name = "body", descKey = ConfigI18nKey.CONFIG_BODY_DESCRIPTION)
    private String              body;

    @ConfigDef(name = "header", descKey = ConfigI18nKey.CONFIG_HEADER_DESCRIPTION)
    private Map<String, String> header;
}
