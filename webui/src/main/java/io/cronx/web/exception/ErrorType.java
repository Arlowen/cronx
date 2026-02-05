package io.cronx.web.exception;

enum ErrorType {

    /**
     * Error code start from 1000-1999
     */
    SYSTEM,
    /**
     * Error code start from 2000-2999
     */
    CONSOLE_JOB,
    /**
     * Error code start from 3000-3999
     */
    DATA_JOB,
    /**
     * Error code start from 4000-4999
     */
    CONFIG,
    /**
     * Error code start from 5000-5999
     */
    ZOOKEEPER,
    /**
     * Error code start from 6000-6999
     */
    DATASOURCE,
    /**
     * Error code start from 7000-7999
     */
    RESOURCE,
    /**
     * Error code start from 8000-8999
     */
    RSOCKET,
    /**
     * Error code start from 9000-9999
     */
    DATA_TASK,
    /**
     * Error code start from 10000~10999
     */
    JAR_PACKAGE;
}
