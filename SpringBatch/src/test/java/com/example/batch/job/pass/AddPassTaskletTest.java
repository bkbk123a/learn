package com.example.batch.job.pass;

import com.example.batch.config.TestBatchConfig;
import com.example.batch.config.job.tasklet.AddPassTasklet;
import com.example.batch.service.PassService;
import com.example.batch.service.UserGroupService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.batch.core.*;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("이용권 지급 테스트")
@SpringBootTest
@ContextConfiguration(classes = {TestBatchConfig.class})
public class AddPassTaskletTest {

  @Mock
  private PassService passService;

  @Mock
  private UserGroupService userGroupService;

  @InjectMocks
  private AddPassTasklet addPassTasklet;

  @Test
  public void givenAddPassTaskletWhenExecuteThenWorksFine() {

    // given - StepContribution, ChunkContext 생성
    StepExecution stepExecution = new StepExecution("AddPassTaskletTest", new JobExecution(1L));
    StepContribution stepContribution = new StepContribution(stepExecution);
    ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

    // when - Tasklet 실행
    RepeatStatus repeatStatus = addPassTasklet.execute(stepContribution, chunkContext);

    // then - Tasklet 실행 결과를 검증
    assertThat(repeatStatus).isEqualTo(RepeatStatus.FINISHED);
  }
}
