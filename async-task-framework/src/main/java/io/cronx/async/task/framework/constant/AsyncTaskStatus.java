package io.cronx.async.task.framework.constant;

/**
 * Task not be aware of middle status like wait start and wait stop or failed status. Job use the middle status
 *
 **/
public enum AsyncTaskStatus {

    /**
     * initial task status
     */
    WAIT_START,

    /**
     * when occur exception
     */
    ABNORMAL,

    /**
     * Only one task in running status
     */
    RUNNING,

    /**
     * end status
     */
    FINISHED
}
