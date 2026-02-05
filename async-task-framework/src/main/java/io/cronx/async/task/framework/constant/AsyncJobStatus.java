package io.cronx.async.task.framework.constant;

public enum AsyncJobStatus {

    /**
     * After job is created and before executed it is in WAIT_START status
     */
    WAIT_START,

    /**
     * If any task is executed, it will in running status
     */
    RUNNING,

    /**
     * If job is failed caused by current stage task, then job is in FAILED status
     */
    FAILED,

    /**
     * If job is finished all subtask, it will in success status
     */
    FINISHED,

    /**
     * If job is stopped, need to start manually 
     */
    STOPPED,;

}
