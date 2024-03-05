package com.example.batch.config.job.tasklet;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

// [ 사용자에게 이용권 지급 ]
// Tasklet을 사용한 Task 기반 처리
//- 배치 처리 과정이 비교적 쉬운 경우
//- Step이 중지될 때까지 execute 메서드가 계속 반복해서 수행하고 수행할 때마다 독립적인 트랜잭션이 얻어진다.
@Configuration
@RequiredArgsConstructor
public class AddPassJobConfig {

    private final AddPassTasklet addPassTasklet;

    @Bean
    public Job addPassJob(JobRepository jobRepository, @Qualifier("addPassStep") Step addPassStep) {
        return new JobBuilder("addPassJob", jobRepository)
                .start(addPassStep)
                .build();
    }

    @Bean
    @Qualifier("addPassStep")
    public Step addPassStep(JobRepository jobRepository, PlatformTransactionManager manager) {
        return new StepBuilder("addPassStep", jobRepository)
                .tasklet(addPassTasklet, manager)
                .build();
    }
}