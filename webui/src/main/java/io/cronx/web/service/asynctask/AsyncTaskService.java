package io.cronx.web.service.asynctask;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import io.cronx.async.task.framework.model.AsyncTaskBase;
import io.cronx.web.model.entity.AsyncJobDO;
import io.cronx.web.model.fo.asynctask.AddTaskFO;
import io.cronx.web.model.vo.asynctask.QueryTaskVO;

@Service
public interface AsyncTaskService {

    void addTask(AddTaskFO fo);

    /**
     * tasks must belong to the same job
     */
    void addTasks(List<AddTaskFO> fos);

    QueryTaskVO listTaskPageByCondition(long jobId, long pageSize, long pageNumber);

    void deleteByTaskId(long taskId);

    void deleteByJobId(long jobId);

    /**
     * Assemble tasks with info from persist info in database
     * <pre>
     *   If timer restart old finished tasks, reset task status to WAIT_START
     * </pre>
     */
    List<AsyncTaskBase> assembleTasksFromDb(AsyncJobDO asyncJobDO, boolean timerTrigger);

}
