package io.cronx.async.task.framework.alert;

public interface AlertService {

    void sendMsg(String msg, AlarmLevel alarmLevel);

    String getMsgPrefix();

}
