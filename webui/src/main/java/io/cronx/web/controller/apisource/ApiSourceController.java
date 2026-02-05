package io.cronx.web.controller.apisource;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.cronx.web.constant.ControllerUrlPrefix;
import io.cronx.web.model.base.ResponseData;
import io.cronx.web.model.base.ResponseDataUtil;
import io.cronx.web.model.fo.apisource.AddApiSourceFO;
import io.cronx.web.model.fo.apisource.DeleteApiSourceFO;
import io.cronx.web.model.fo.apisource.QueryApiSourceFO;
import io.cronx.web.model.vo.apisource.QueryApiSourceVO;
import io.cronx.web.service.apisource.ApiSourceService;

@RestController
@RequestMapping(ControllerUrlPrefix.CONSOLE_PREFIX + "/apisource")
public class ApiSourceController {

    @Resource
    private ApiSourceService apiSourceService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseData<?> addApiSource(@Valid @RequestBody AddApiSourceFO fo, HttpServletRequest request) {
        apiSourceService.addApiSource(fo);
        return ResponseDataUtil.buildSuccess();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseData<?> deleteApiSource(@Valid @RequestBody DeleteApiSourceFO fo, HttpServletRequest request) {
        apiSourceService.deleteApiSource(fo.getApiSourceId());
        return ResponseDataUtil.buildSuccess();
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ResponseData<?> listApiSource(@Valid @RequestBody QueryApiSourceFO fo) {
        QueryApiSourceVO sourceVO = apiSourceService.listApiSourcePageByCondition(fo.getPageSize(), fo.getPageNumber());
        return ResponseDataUtil.buildSuccess(sourceVO);
    }
}
