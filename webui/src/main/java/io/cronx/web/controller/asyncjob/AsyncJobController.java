package io.cronx.web.controller.asyncjob;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import io.cronx.web.model.fo.asyncjob.StartJobFO;
import io.cronx.web.service.user.OptUserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.cronx.web.constant.ControllerUrlPrefix;
import io.cronx.web.model.base.ResponseData;
import io.cronx.web.model.base.ResponseDataUtil;
import io.cronx.web.model.fo.asyncjob.AddJobFO;
import io.cronx.web.model.fo.asyncjob.DeleteJobFO;
import io.cronx.web.model.fo.asyncjob.QueryJobFO;
import io.cronx.web.model.vo.asyncjob.QueryJobVO;
import io.cronx.web.service.asyncjob.AsyncJobService;

@RestController
@RequestMapping(ControllerUrlPrefix.CONSOLE_PREFIX + "/asyncjob")
public class AsyncJobController {

    @Resource
    private AsyncJobService asyncJobService;

    @RequestMapping(value = "/list", method = { RequestMethod.POST })
    public ResponseData<?> listJob(@Valid @RequestBody QueryJobFO fo) {
        QueryJobVO queryJobVO = asyncJobService.listJobPageByCondition(fo.getPageSize(), fo.getPageNumber());
        return ResponseDataUtil.buildSuccess(queryJobVO);
    }

    @RequestMapping(value = "/create", method = { RequestMethod.POST })
    public ResponseData<?> createJob(@Valid @RequestBody AddJobFO fo, HttpServletRequest request) {
        asyncJobService.createJob(fo.getJobDesc());
        return ResponseDataUtil.buildSuccess();
    }

    @RequestMapping(value = "/deletebyjobid", method = { RequestMethod.POST })
    public ResponseData<?> deleteByJobId(@Valid @RequestBody DeleteJobFO fo) {
        asyncJobService.deleteByJobId(fo.getJobId());
        return ResponseDataUtil.buildSuccess();
    }

    @RequestMapping(value = "/startjob", method = { RequestMethod.POST })
    public ResponseData<?> startjob(@Valid @RequestBody StartJobFO fo) {
        asyncJobService.startAsyncJob(fo.getJobId());
        return ResponseDataUtil.buildSuccess();
    }

    @RequestMapping(value = "/stopjob", method = { RequestMethod.POST })
    public ResponseData<?> stopjob(@Valid @RequestBody StartJobFO fo) {
        asyncJobService.stopAsyncJob(fo.getJobId());
        return ResponseDataUtil.buildSuccess();
    }
}
