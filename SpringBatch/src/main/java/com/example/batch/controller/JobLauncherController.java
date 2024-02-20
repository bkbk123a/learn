package com.example.batch.controller;

import com.example.batch.request.JobLauncherRequest;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("job")
@RequiredArgsConstructor
public class JobLauncherController {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @PostMapping("/launcher")
    public ExitStatus luanchJob(@RequestBody JobLauncherRequest request) throws Exception {
        Job job = jobRegistry.getJob(request.getRequestName());
        return jobLauncher.run(job, request.getJobParameters()).getExitStatus();    // name 에 해당 하는 job 실행
    }
}
