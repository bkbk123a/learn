package com.example.batch.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

@Getter
@Setter
public class JobLauncherRequest {
    private String requestName;
    private JobParameters jobParameters;    // job 에 전달할 변수

    public JobParameters getJobParameters() {
        return new JobParametersBuilder(jobParameters).toJobParameters();
    }
}
