package io.cronx.web.service.apisource.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.cronx.toolkit.utils.CollectionUtils;
import io.cronx.toolkit.utils.ExceptionUtils;
import io.cronx.web.component.config.ApiSourceConfig;
import io.cronx.web.mapper.ApiKvBaseConfigMapper;
import io.cronx.web.mapper.ApiSourceMapper;
import io.cronx.web.model.entity.ApiKvBaseConfigDO;
import io.cronx.web.model.entity.ApiSourceDO;
import io.cronx.web.model.fo.apisource.AddApiSourceFO;
import io.cronx.web.model.mo.GenCoreConfigMO;
import io.cronx.web.model.vo.apisource.ApiSourceVO;
import io.cronx.web.model.vo.apisource.QueryApiSourceVO;
import io.cronx.web.service.apisource.ApiSourceService;
import io.cronx.web.service.config.ApiConfigHelper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApiSourceServiceImpl implements ApiSourceService {

    @Resource
    private ApiSourceMapper       apiSourceMapper;

    @Resource
    private ApiKvBaseConfigMapper apiKvBaseConfigMapper;

    @Resource
    private ApiConfigHelper       configHelper;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void addApiSource(AddApiSourceFO apiSourceFO) {
        long apiSourceId = apiSourceMapper.insert(apiSourceFO.genApiSourceDO());

        GenCoreConfigMO configMO = new GenCoreConfigMO();
        configMO.setBody(apiSourceFO.getBody());
        configMO.setParams(apiSourceFO.getParams());
        configMO.setCookies(apiSourceFO.getCookies());
        configMO.setRequestType(apiSourceFO.getRequestType());
        collectConfig(apiSourceId, configMO);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteApiSource(long apiSourceId) {
        apiSourceMapper.deleteById(apiSourceId);
        apiKvBaseConfigMapper.deleteApiSourceConfig(apiSourceId);
    }

    @Override
    public QueryApiSourceVO listApiSourcePageByCondition(long pageSize, long pageNum) {
        long offset = (pageNum - 1) * pageSize;
        List<ApiSourceDO> sourceDOS = apiSourceMapper.queryByCondition(offset, pageSize);

        if (sourceDOS.isEmpty()) {
            return new QueryApiSourceVO(new ArrayList<>(), 0L);
        }

        List<ApiSourceVO> apiSourceVOS = sourceDOS.stream().map(sourceDO -> {
            ApiSourceVO apiSourceVO = new ApiSourceVO();
            apiSourceVO.convertVO(sourceDO);
            return apiSourceVO;
        }).collect(Collectors.toList());

        Integer totalCount = apiSourceMapper.queryCountByCondition();
        return new QueryApiSourceVO(apiSourceVOS, totalCount);
    }

    @Override
    public ApiSourceConfig fetchApiConfig(long apiSourceId) {
        ApiSourceDO apiSourceDO = apiSourceMapper.selectById(apiSourceId);
        if (apiSourceDO == null) {
            return null;
        }

        List<ApiKvBaseConfigDO> source = apiKvBaseConfigMapper.queryListByApiSourceId(apiSourceId);

        Map<String, String> configMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(source)) {
            for (ApiKvBaseConfigDO configDO : source) {
                configMap.put(configDO.getConfigName(), configDO.getConfigValue());
            }
        }

        ApiSourceConfig config = new ApiSourceConfig();
        config.setHost(apiSourceDO.getHost());
        configHelper.fillFieldValue(config, configMap);
        return config;
    }

    private void collectConfig(long apiSourceId, GenCoreConfigMO configMO) {
        collectConfigs(Collections.singletonMap(apiSourceId, configMO));
    }

    private void collectConfigs(Map<Long, GenCoreConfigMO> coreConfigMap) {
        try {
            List<ApiKvBaseConfigDO> allCoreConfig = new LinkedList<>();
            for (Map.Entry<Long, GenCoreConfigMO> entry : coreConfigMap.entrySet()) {
                ApiSourceConfig apiSourceConfig = new ApiSourceConfig();

                if (entry.getValue().getBody() != null) {
                    apiSourceConfig.setBody(entry.getValue().getBody());
                }

                if (entry.getValue().getCookies() != null) {
                    apiSourceConfig.setCookies(entry.getValue().getCookies());
                }

                if (entry.getValue().getBody() != null) {
                    apiSourceConfig.setBody(entry.getValue().getBody());
                }

                if (entry.getValue().getBody() != null) {
                    apiSourceConfig.setBody(entry.getValue().getBody());
                }

                List<ApiKvBaseConfigDO> coreConfigs = configHelper.collectConfigs(apiSourceConfig, entry.getKey());

                allCoreConfig.addAll(coreConfigs);
            }

            apiKvBaseConfigMapper.batchInsertConfig(allCoreConfig);
        } catch (Exception e) {
            String msg = "Persist server core config failed,msg:" + ExceptionUtils.getRootCauseMessage(e);
            log.error(msg, e);
            throw new IllegalArgumentException(msg, e);
        }
    }
}
