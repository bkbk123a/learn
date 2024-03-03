package com.example.batch.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaAuditing        // auditing 설정
@EnableAutoConfiguration  // 미리 정의된 자바 설정파일(@Configuration)들을 빈으로 등록
@EntityScan("com.example.batch.entity")
@EnableJpaRepositories("com.example.batch.repository")
@EnableTransactionManagement
public class TestBatchConfig {
}
