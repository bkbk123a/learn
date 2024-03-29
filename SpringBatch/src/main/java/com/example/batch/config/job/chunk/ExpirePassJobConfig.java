package com.example.batch.config.job.chunk;

import com.example.batch.entity.pass.UserPass;
import com.example.batch.enumerator.PassStatus;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;

// [ 사용자의 이용권 만료 ]
// Chunk를 사용한 chunk(덩어리) 기반 처리
// ItemReader, ItemProcessor, ItemWriter의 관계 이해 필요
// 예를 들면 10,000개의 데이터 중 1,000개씩 10개의 덩어리로 수행
@Configuration
@RequiredArgsConstructor
public class ExpirePassJobConfig {

  // Spring Batch에서 트랜잭션 범위는 Chunk단위
  private final int CHUNK_SIZE = 5;
  private final EntityManagerFactory entityManagerFactory;

  @Bean
  public Job expirePassJob(JobRepository jobRepository, @Qualifier("expirePassStep") Step expirePassStep) {
    return new JobBuilder("expirePassJob", jobRepository)
        .start(expirePassStep)
        .build();
  }

  @Bean
  @Qualifier("expirePassStep")
  public Step expirePassStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("expirePassStep", jobRepository)
        .<UserPass, UserPass>chunk(CHUNK_SIZE, transactionManager)
        .reader(expirePassItemReader())
        .processor(expirePassItemProcessor())
        .writer(expireItemWriter())
        .build();
  }

  @Bean
  @JobScope
  public JpaCursorItemReader<UserPass> expirePassItemReader() {
    return new JpaCursorItemReaderBuilder<UserPass>()
        .name("expirePassItemReader")
        .entityManagerFactory(entityManagerFactory)
        // expireAt(만료시간) 이전의 PROGRESS 상태인 이용권 조회
        .queryString("SELECT p FROM UserPass AS p " +
            "WHERE p.passStatus = :passStatus " +
            "AND p.expiredAt <= :expiredAt")
        .parameterValues(Map.of("passStatus", PassStatus.PROGRESS, "expiredAt", LocalDateTime.now()))
        .build();
  }

  // Reader에서 넘겨준 데이터 개별 건을 가공/처리
  @Bean
  public ItemProcessor<UserPass, UserPass> expirePassItemProcessor() {
    return pass -> {
      pass.setPassStatus(PassStatus.EXPIRED); // 만료 처리
      pass.setExpiredAt(LocalDateTime.now());
      return pass;
    };
  }

  // JpaItemWriter: JPA의 영속성 관리를 위해 EntityManager를 필수로 설정
  // ChunkSize 단위로 묶은 데이터를 한번에 처리
  @Bean
  public JpaItemWriter<UserPass> expireItemWriter() {
    return new JpaItemWriterBuilder<UserPass>()
        .entityManagerFactory(entityManagerFactory)
        .build();
  }
}
