package io.cronx.async.task.framework.model;

public interface AsyncJobBase extends Runnable {

    /**
     * Auto persist before and after execute
     */
    void executeJob();

    void stopJob();

    boolean isEndStatus();

    /**
     * Persist job exec result's snapshot
     */
    void persistSnapshot(String errMsg);

    /**
     * In end status, remove cache in job manager
     */
    void removeFromCache();

}
