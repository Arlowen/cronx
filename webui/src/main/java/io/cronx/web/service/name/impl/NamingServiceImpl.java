package io.cronx.web.service.name.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import io.cronx.toolkit.utils.RandomStrUtils;
import io.cronx.web.mapper.AsyncJobMapper;
import io.cronx.web.mapper.AsyncTaskMapper;
import io.cronx.web.model.entity.AsyncJobDO;
import io.cronx.web.model.entity.AsyncTaskDO;
import io.cronx.web.service.name.NamingService;

@Service
public class NamingServiceImpl implements NamingService {

    @Resource
    private AsyncJobMapper  jobMapper;

    @Resource
    private AsyncTaskMapper taskMapper;

    @Override
    public String genJobName() {
        String jobIdPattern;
        int length = 11;
        jobIdPattern = "cronx%s";

        while (true) {
            String jobName = String.format(jobIdPattern, RandomStrUtils.fixedLenRandomStr(length));
            // check unique
            AsyncJobDO jobDO = jobMapper.queryJobByName(jobName);
            if (jobDO == null) {
                return jobName;
            }
        }
    }

    @Override
    public String genTaskName(String jobName) {
        String jobIdPattern;
        int length = 11;
        jobIdPattern = jobName + "_%s";

        while (true) {
            String taskName = String.format(jobIdPattern, RandomStrUtils.fixedLenRandomNumberStr(length));
            // check unique
            AsyncTaskDO taskDO = taskMapper.queryTaskByName(taskName);
            if (taskDO == null) {
                return taskName;
            }
        }
    }
}
