package com.example.batch.job.pass;

import com.example.batch.config.TestBatchConfig;
import com.example.batch.config.job.tasklet.AddPassJobConfig;
import com.example.batch.config.job.tasklet.AddPassTasklet;
import com.example.batch.entity.pass.Pass;
import com.example.batch.entity.pass.UserGroup;
import com.example.batch.enumerator.ProvidePassStatus;
import com.example.batch.repository.pass.PassRepository;
import com.example.batch.repository.pass.UserGroupRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBatchTest
@SpringBootTest
@ContextConfiguration(classes = {AddPassJobConfig.class, AddPassTasklet.class, TestBatchConfig.class})
public class AddPassJobConfigTest {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  private PassRepository passRepository;

  @Autowired
  private UserGroupRepository userGroupRepository;

  @Test
  public void givenPass_whenExecuteAddPassJob_ThenWorksFine() throws Exception {
    // given
    Pass pass = addPass();

    // when
    JobExecution jobExecution = jobLauncherTestUtils.launchJob();
    JobInstance jobInstance = jobExecution.getJobInstance();

    // then
    assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus()); // job 정상 종료 확인
    assertEquals("addPassJob", jobInstance.getJobName());

    Pass providedPass = passRepository.findById(pass.getPassId()).get();
    assertEquals(ProvidePassStatus.COMPLETED, providedPass.getPassStatus());  // 이용권 지급 완료 후, 이용권 지급 상태 완료 확인
  }

  /**
   * 테스트 데이터 추가
   */
  private Pass addPass() {
    final LocalDateTime now = LocalDateTime.now();
    final int userGroupId = 2;
    final long userId = 999;

    Pass pass = Pass.builder()
        .userGroupId(userGroupId)
        .passStatus(ProvidePassStatus.READY)
        .count(10)
        .startedAt(now)
        .expiredAt(now.plusDays(30))
        .build();

    UserGroup userGroup = UserGroup.builder()
        .userGroupId(userGroupId)
        .userId(userId)
        .userGroupName("테스트 코드")
        .build();

    userGroupRepository.save(userGroup);
    return passRepository.save(pass);
  }
}
