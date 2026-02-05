package io.cronx.web.service.apisource;

import org.springframework.stereotype.Service;

import io.cronx.web.component.config.ApiSourceConfig;
import io.cronx.web.model.fo.apisource.AddApiSourceFO;
import io.cronx.web.model.vo.apisource.QueryApiSourceVO;

@Service
public interface ApiSourceService {

    void addApiSource(AddApiSourceFO apiSourceFO);

    void deleteApiSource(long apiSourceId);

    QueryApiSourceVO listApiSourcePageByCondition(long pageSize, long pageNumber);

    ApiSourceConfig fetchApiConfig(long apiSourceId);
}
