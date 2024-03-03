package com.example.batch.job.pass;

import com.example.batch.config.TestBatchConfig;
import com.example.batch.config.job.chunk.ExpirePassJobConfig;
import com.example.batch.entity.pass.UserPass;
import com.example.batch.enumerator.PassStatus;
import com.example.batch.repository.pass.UserPassRepository;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBatchTest
@SpringBootTest
@ContextConfiguration(classes = {ExpirePassJobConfig.class, TestBatchConfig.class})
public class ExpirePassesJobConfigTest {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  private UserPassRepository userPassRepository;

  private final long START_USER_ID = 100000L;
  private final int TEST_USER_SIZE = 5;

  @Test
  public void givenUserPass_whenExecuteExpirePassJob_thenExpirePass() throws Exception {

    // given
    deleteUserPassBeforeTest(); // 테스트 데이터 존재시 삭제
    addUserPass();              // 테스트 데이터 add

    // when                     // job 실행
    JobExecution jobExecution = jobLauncherTestUtils.launchJob();
    JobInstance jobInstance = jobExecution.getJobInstance();

    // then
    assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus()); // job 정상 종료
    assertEquals("expirePassJob", jobInstance.getJobName());
  }

  private void deleteUserPassBeforeTest() {
    List<Long> userIds = new ArrayList<>();
    for (int i = 0; i < TEST_USER_SIZE; i++) {
      userIds.add(START_USER_ID + i);
    }

    userPassRepository.deleteAllByUserIdIn(userIds);
  }

  private void addUserPass() {
    final LocalDateTime now = LocalDateTime.now();
    final Random random = new Random();

    List<UserPass> userPasses = new ArrayList<>();
    for (int i = 0; i < TEST_USER_SIZE; i++) {
      userPasses.add(UserPass.builder()
          .passId(1)
          .userId(START_USER_ID + i)
          .passStatus(PassStatus.PROGRESS)
          .remainingCount(random.nextInt(10))
          .startedAt(now.minusDays(30))
          .expiredAt(now.minusDays(1))
          .build());
    }

    userPassRepository.saveAll(userPasses);
  }
}
