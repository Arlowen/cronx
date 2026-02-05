package io.cronx.web.service.config.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import io.cronx.toolkit.utils.BeanUtils;
import io.cronx.toolkit.utils.convert.ConverterUtils;
import io.cronx.web.annotation.ConfigDef;
import io.cronx.web.model.entity.KvBaseConfigDO;
import io.cronx.web.service.config.AsyncTaskConfigHelper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AsyncTaskConfigHelperImpl implements AsyncTaskConfigHelper {

    @Override
    public void fillFieldValue(Object instance, Map<String, String> configMap) {
        fillFieldValue(instance, instance.getClass(), configMap);
    }

    @Override
    public List<KvBaseConfigDO> collectConfigs(Object instance, Long taskId) {
        List<KvBaseConfigDO> configs = new ArrayList<>();
        collectConfigs(instance, taskId, instance.getClass(), configs);
        return configs;
    }

    protected void collectConfigs(Object instance, Long taskId, Class clazz, List<KvBaseConfigDO> configs) {
        try {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);

                ConfigDef configDef = field.getAnnotation(ConfigDef.class);
                if (configDef == null) {
                    continue;
                }

                String val = configDef.defaultValue();
                Object oriVal = field.get(instance);
                if (oriVal != null) {
                    val = String.valueOf(oriVal);
                }

                KvBaseConfigDO configDO = genConfigDo(configDef, val, taskId);

                configs.add(configDO);
            }

            if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
                collectConfigs(instance, taskId, clazz.getSuperclass(), configs);
            }
        } catch (Exception e) {
            String msg = "collect field value failed,msg:" + ExceptionUtils.getRootCauseMessage(e);
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    protected KvBaseConfigDO genConfigDo(ConfigDef configDef, String val, Long taskId) {
        KvBaseConfigDO configDO = new KvBaseConfigDO();
        configDO.setConfigName(configDef.name());
        configDO.setConfigValue(val);
        configDO.setTaskId(taskId);
        configDO.setValueRange(configDef.valueRange());

        configDO.setDefaultValue(configDef.defaultValue());
        configDO.setDescKey(configDef.descKey().name());
        return configDO;
    }

    protected void fillFieldValue(Object instance, Class clazz, Map<String, String> configMap) {
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            ConfigDef configDef = field.getAnnotation(ConfigDef.class);
            if (configDef == null) {
                continue;
            }

            String configValue = configMap.get(configDef.name());

            if (configValue == null) {
                continue;
            }

            Object convert = ConverterUtils.convert(configValue, field.getType());
            if (convert == null && field.getType().isPrimitive()) {
                convert = BeanUtils.getDefaultValue(field.getType());
            }

            try {
                if (StringUtils.isBlank(configValue)) {
                    field.set(instance, null);
                } else {
                    field.set(instance, convert);
                }
            } catch (Exception e) {
                String msg = "fill field value failed,msg:" + ExceptionUtils.getRootCauseMessage(e);
                log.error(msg, e);
                throw new RuntimeException(msg, e);
            }

        }

        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            fillFieldValue(instance, clazz.getSuperclass(), configMap);
        }
    }
}
