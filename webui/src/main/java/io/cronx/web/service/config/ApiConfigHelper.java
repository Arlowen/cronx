package io.cronx.web.service.config;

import java.util.List;
import java.util.Map;

import io.cronx.web.model.entity.ApiKvBaseConfigDO;

public interface ApiConfigHelper {

    void fillFieldValue(Object instance, Map<String, String> configMap);

    List<ApiKvBaseConfigDO> collectConfigs(Object instance, Long taskId);
}
