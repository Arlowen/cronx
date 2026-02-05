package io.cronx.web.service.asynctask.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.cronx.async.task.framework.constant.AsyncTaskStatus;
import io.cronx.async.task.framework.model.AsyncTaskBase;
import io.cronx.toolkit.utils.ExceptionUtils;
import io.cronx.web.asynctask.ApiRequestTask;
import io.cronx.web.asynctask.context.ApiRequestContext;
import io.cronx.web.asynctask.trigger.AlwaysTrueTrigger;
import io.cronx.web.component.config.ApiSourceConfig;
import io.cronx.web.component.config.AsyncTaskConfig;
import io.cronx.web.mapper.ApiSourceMapper;
import io.cronx.web.mapper.AsyncJobMapper;
import io.cronx.web.mapper.AsyncTaskMapper;
import io.cronx.web.mapper.KvBaseConfigMapper;
import io.cronx.web.model.entity.ApiSourceDO;
import io.cronx.web.model.entity.AsyncJobDO;
import io.cronx.web.model.entity.AsyncTaskDO;
import io.cronx.web.model.entity.KvBaseConfigDO;
import io.cronx.web.model.enumeration.LifeCycleState;
import io.cronx.web.model.fo.asynctask.AddTaskFO;
import io.cronx.web.model.mo.GenCoreConfigMO;
import io.cronx.web.model.vo.asynctask.AsyncTaskVO;
import io.cronx.web.model.vo.asynctask.QueryTaskVO;
import io.cronx.web.service.apisource.ApiSourceService;
import io.cronx.web.service.asynctask.AsyncTaskService;
import io.cronx.web.service.config.AsyncTaskConfigHelper;
import io.cronx.web.service.name.NamingService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AsyncTaskServiceImpl implements AsyncTaskService {

    @Resource
    private AsyncTaskMapper       taskMapper;

    @Resource
    private AsyncJobMapper        jobMapper;

    @Resource
    private ApiSourceMapper       apiSourceMapper;

    @Resource
    private NamingService         namingService;

    @Resource
    private AsyncTaskConfigHelper configHelper;

    @Resource
    private KvBaseConfigMapper    kvBaseConfigMapper;

    @Resource
    private ApiSourceService      apiSourceService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void addTask(AddTaskFO fo) {
        AsyncJobDO jobDO = jobMapper.queryJobById(fo.getJobId());
        if (jobDO == null) {
            throw new IllegalArgumentException("AsyncJob not founded, jobId: " + fo.getJobId());
        }

        // add task
        AsyncTaskDO taskDO = convertTaskDO(fo, jobDO);
        long taskId = taskMapper.insert(taskDO);

        // gen config for task
        ApiSourceConfig apiConfig = apiSourceService.fetchApiConfig(fo.getApiSourceId());
        GenCoreConfigMO configMO = new GenCoreConfigMO();
        if (apiConfig == null) {
            configMO.setBody(fo.getBody());
            configMO.setParams(fo.getParams());
            configMO.setCookies(fo.getCookies());
            configMO.setRequestType(fo.getRequestType());
            configMO.setHost(fo.getHost());
        } else {
            configMO.setBody(apiConfig.getBody());
            configMO.setParams(apiConfig.getParams());
            configMO.setCookies(apiConfig.getCookies());
            configMO.setRequestType(apiConfig.getRequestType());
            configMO.setHost(apiConfig.getHost());
        }
        collectConfig(taskId, configMO);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void addTasks(List<AddTaskFO> fos) {
        AddTaskFO fo = fos.get(0);

        ApiSourceDO apiSrcDO = apiSourceMapper.selectById(fo.getApiSourceId());

        if (apiSrcDO == null) {
            throw new IllegalArgumentException("ApiSource not founded, apiSourceId: " + fo.getApiSourceId());
        }

        AsyncJobDO jobDO = jobMapper.queryJobById(fo.getJobId());

        if (jobDO == null) {
            throw new IllegalArgumentException("AsyncJob not founded, jobId: " + fo.getJobId());
        }

        List<AsyncTaskDO> tasks = fos.stream().map(taskFO -> convertTaskDO(taskFO, jobDO)).collect(Collectors.toList());

        taskMapper.batchInsertTask(tasks);

        Map<Long, AsyncTaskDO> taskMap = tasks.stream().collect(Collectors.toMap(AsyncTaskDO::getExecOrder, taskDO -> taskDO));

        Map<Long, GenCoreConfigMO> coreConfigMap = fos.stream()
            .peek(taskFO -> taskFO.setTaskId(taskMap.get(taskFO.getExecOrder()).getId()))
            .collect(Collectors.toMap(AddTaskFO::getTaskId, taskFO -> {
                GenCoreConfigMO configMO = new GenCoreConfigMO();
                configMO.setBody(taskFO.getBody());
                configMO.setParams(taskFO.getParams());
                configMO.setCookies(taskFO.getCookies());
                configMO.setRequestType(taskFO.getRequestType());
                return configMO;
            }));

        collectConfigs(coreConfigMap);
    }

    private void collectConfig(long taskId, GenCoreConfigMO configMO) {
        collectConfigs(Collections.singletonMap(taskId, configMO));
    }

    private void collectConfigs(Map<Long, GenCoreConfigMO> coreConfigMap) {
        try {
            List<KvBaseConfigDO> allCoreConfig = new LinkedList<>();
            for (Map.Entry<Long, GenCoreConfigMO> entry : coreConfigMap.entrySet()) {
                AsyncTaskConfig taskConfig = new AsyncTaskConfig();

                if (entry.getValue().getBody() != null) {
                    taskConfig.setBody(entry.getValue().getBody());
                }

                if (entry.getValue().getCookies() != null) {
                    taskConfig.setCookies(entry.getValue().getCookies());
                }

                if (entry.getValue().getHost() != null) {
                    taskConfig.setHost(entry.getValue().getHost());
                }

                if (entry.getValue().getCookies() != null) {
                    taskConfig.setCookies(entry.getValue().getCookies());
                }

                if (entry.getValue().getRequestType() != null) {
                    taskConfig.setRequestType(entry.getValue().getRequestType());
                }

                List<KvBaseConfigDO> coreConfigs = configHelper.collectConfigs(taskConfig, entry.getKey());

                allCoreConfig.addAll(coreConfigs);
            }
            kvBaseConfigMapper.batchInsertConfig(allCoreConfig);
        } catch (Exception e) {
            String msg = "Persist server core config failed,msg:" + ExceptionUtils.getRootCauseMessage(e);
            log.error(msg, e);
            throw new IllegalArgumentException(msg, e);
        }
    }

    private AsyncTaskDO convertTaskDO(AddTaskFO fo, AsyncJobDO jobDO) {
        AsyncTaskDO taskDO = new AsyncTaskDO();
        taskDO.setTaskStatus(AsyncTaskStatus.WAIT_START);
        taskDO.setJobId(jobDO.getId());
        taskDO.setLifeCycleState(LifeCycleState.CREATED);
        // TODO
        taskDO.setTaskName(namingService.genTaskName(jobDO.getJobName()));
        taskDO.setExecOrder(fo.getExecOrder());
        return taskDO;
    }

    @Override
    public QueryTaskVO listTaskPageByCondition(long jobId, long pageSize, long pageNum) {
        long offset = (pageNum - 1) * pageSize;
        List<AsyncTaskDO> taskDOS = taskMapper.queryByCondition(offset, pageSize, jobId);

        if (taskDOS.isEmpty()) {
            return new QueryTaskVO(new ArrayList<>(), 0L);
        }

        List<AsyncTaskVO> asyncTaskVOS = taskDOS.stream().map(taskDO -> {
            AsyncTaskVO asyncTaskVO = new AsyncTaskVO();
            asyncTaskVO.convertVO(taskDO);
            return asyncTaskVO;
        }).collect(Collectors.toList());

        Integer totalCount = taskMapper.queryCountByCondition(jobId);
        return new QueryTaskVO(asyncTaskVOS, totalCount);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteByTaskId(long taskId) {
        AsyncTaskDO taskDO = taskMapper.queryByTaskId(taskId);

        if (taskDO == null) {
            throw new IllegalArgumentException("AsyncTask not founded, taskId: " + taskId);
        }

        if (taskDO.getTaskStatus() == AsyncTaskStatus.RUNNING) {
            throw new IllegalStateException("AsyncTask is RUNNING, stop it first");
        }

        taskMapper.deleteByTaskId(taskId);

        kvBaseConfigMapper.deleteTaskConfig(taskId);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteByJobId(long jobId) {
        List<AsyncTaskDO> taskDOS = taskMapper.queryListByJobId(jobId);

        if (taskDOS.isEmpty()) {
            return;
        }

        taskMapper.deleteByJobId(jobId);

        Set<Long> taskIds = taskDOS.stream().map(AsyncTaskDO::getId).collect(Collectors.toSet());

        kvBaseConfigMapper.deleteByTaskIds(taskIds);
    }

    @Override
    public List<AsyncTaskBase> assembleTasksFromDb(AsyncJobDO asyncJobDO, boolean timerTrigger) {
        long asyncJobId = asyncJobDO.getId();

        Map<Long, AsyncTaskStatus> taskOrderStatusMap = new HashMap<>();
        List<AsyncTaskDO> asyncTaskDOs = taskMapper.queryListByJobId(asyncJobId);
        if (timerTrigger) {
            asyncTaskDOs.forEach(taskDO -> taskDO.setTaskStatus(AsyncTaskStatus.WAIT_START));
        }

        asyncTaskDOs.forEach(taskDO -> taskOrderStatusMap.put(taskDO.getId(), taskDO.getTaskStatus()));

        List<AsyncTaskBase> tasks = new ArrayList<>();
        for (AsyncTaskDO taskDO : asyncTaskDOs) {
            ApiRequestContext context = new ApiRequestContext(asyncJobId,
                taskDO.getId(),
                taskDO.getExecOrder().intValue(),
                new AlwaysTrueTrigger(),
                taskOrderStatusMap.get(taskDO.getId()),
                null,
                new AsyncTaskConfig());
            tasks.add(new ApiRequestTask(context));
        }
        return tasks;
    }

}
