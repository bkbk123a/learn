package com.example.batch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing  // JPA auditing 기능 활성화. BaseEntity 관련(생성, 수정 일시).
@Configuration
public class JpaConfig {
}
