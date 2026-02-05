package io.cronx.web.service.config;

import java.util.List;
import java.util.Map;

import io.cronx.web.model.entity.KvBaseConfigDO;

public interface AsyncTaskConfigHelper {

    void fillFieldValue(Object instance, Map<String, String> configMap);

    List<KvBaseConfigDO> collectConfigs(Object instance, Long taskId);
}
