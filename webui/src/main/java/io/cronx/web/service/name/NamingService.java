package io.cronx.web.service.name;

public interface NamingService {

    String genJobName();

    String genTaskName(String jobName);
}
