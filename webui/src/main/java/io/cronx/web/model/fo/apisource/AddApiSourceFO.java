package io.cronx.web.model.fo.apisource;

import java.util.Map;

import javax.validation.constraints.NotBlank;

import io.cronx.web.constant.api.RequestMethod;
import io.cronx.web.model.entity.ApiSourceDO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddApiSourceFO {

    @NotBlank(message = "{notnull.host}")
    private String              host;

    @NotBlank(message = "{notnull.requesttype}")
    private RequestMethod       requestType;

    private Map<String, Object> cookies;

    private Map<String, Object> params;

    private Map<String, Object> header;

    private String              body;

    public ApiSourceDO genApiSourceDO() {
        ApiSourceDO apiSourceDO = new ApiSourceDO();
        apiSourceDO.setHost(this.host);
        return apiSourceDO;
    }
}
