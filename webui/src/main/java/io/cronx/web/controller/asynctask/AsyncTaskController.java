package io.cronx.web.controller.asynctask;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import io.cronx.web.service.user.OptUserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.cronx.web.constant.ControllerUrlPrefix;
import io.cronx.web.model.base.ResponseData;
import io.cronx.web.model.base.ResponseDataUtil;
import io.cronx.web.model.fo.asynctask.AddTaskFO;
import io.cronx.web.model.fo.asynctask.DeleteTaskFO;
import io.cronx.web.model.fo.asynctask.QueryTaskFO;
import io.cronx.web.model.vo.asynctask.QueryTaskVO;
import io.cronx.web.service.asynctask.AsyncTaskService;

@RestController
@RequestMapping(ControllerUrlPrefix.CONSOLE_PREFIX + "/asynctask")
public class AsyncTaskController {

    @Resource
    private AsyncTaskService taskService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseData<?> addTask(@Valid @RequestBody AddTaskFO fo, HttpServletRequest request) {
        taskService.addTask(fo);
        return ResponseDataUtil.buildSuccess();
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ResponseData<?> listTask(@Valid @RequestBody QueryTaskFO fo) {
        QueryTaskVO taskVO = taskService.listTaskPageByCondition(fo.getJobId(), fo.getPageSize(), fo.getPageNumber());
        return ResponseDataUtil.buildSuccess(taskVO);
    }

    @RequestMapping(value = "/deletetaskbyid", method = RequestMethod.POST)
    public ResponseData<?> deleteTaskById(@Valid @RequestBody DeleteTaskFO fo) {
        taskService.deleteByTaskId(fo.getTaskId());
        return ResponseDataUtil.buildSuccess();
    }

}
