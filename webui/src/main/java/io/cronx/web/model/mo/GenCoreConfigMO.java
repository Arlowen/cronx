package io.cronx.web.model.mo;

import io.cronx.web.constant.api.RequestMethod;
import lombok.Data;

import java.util.Map;

@Data
public class GenCoreConfigMO {

    private String              host;

    private RequestMethod       requestType;

    private Map<String, Object> cookies;

    private Map<String, Object> params;

    private String              body;

}
