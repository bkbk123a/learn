package com.example.batch.config.job.tasklet;

import com.example.batch.entity.pass.Pass;
import com.example.batch.entity.pass.UserGroup;
import com.example.batch.enumerator.ProvidePassStatus;
import com.example.batch.service.PassService;
import com.example.batch.service.UserGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

// Tasklet을 사용한 Task 기반 처리
//- 배치 처리 과정이 비교적 쉬운 경우
//- Step이 중지될 때까지 execute 메서드가 계속 반복해서 수행하고 수행할 때마다 독립적인 트랜잭션이 얻어진다.
@Slf4j
@Component
@RequiredArgsConstructor
public class AddPassTasklet implements Tasklet {

  private final PassService passService;

  private final UserGroupService userGroupService;

  // user group 내 각 사용자들에게 이용권 지급.
  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    final LocalDateTime now = LocalDateTime.now();
    // Ready 상태의 이용권 조회
    final List<Pass> passes = passService.getNowPasses(ProvidePassStatus.READY, now);
    // 이용권 지급할 userGroup 조회
    final List<Integer> userGroupIds = passService.getUserGroupIdsFromPasses(passes);
    final List<UserGroup> userGroups = userGroupService.getUserGroups(userGroupIds);
    // 이용권 지급
    long addedPassCount = passService.addPasses(passes, userGroups);

    log.info("AddPassTasklet - execute: 이용권 {}건 추가 완료, startedAt={}", addedPassCount, now);
    return RepeatStatus.FINISHED;
  }
}
