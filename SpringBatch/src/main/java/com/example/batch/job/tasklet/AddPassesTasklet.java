package com.example.batch.job.tasklet;

import com.example.batch.entity.pass.Pass;
import com.example.batch.entity.pass.UserGroup;
import com.example.batch.entity.pass.UserPass;
import com.example.batch.enumerator.PassStatus;
import com.example.batch.repository.pass.PassRepository;
import com.example.batch.repository.pass.UserGroupRepository;
import com.example.batch.repository.pass.UserPassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddPassesTasklet implements Tasklet {

  private final PassRepository passRepository;
  private final UserPassRepository userPassRepository;
  private final UserGroupRepository userGroupRepository;

  // user group 내 각 사용자들에게 이용권 지급.
  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    final LocalDateTime startedAt = LocalDateTime.now();
    // Ready 상태의 이용권 조회
    final List<Pass> passes = getNowPasses(PassStatus.READY, startedAt);
    // 이용권 지급할 userGroup 조회
    final List<Integer> userGroupIds = getUserGroupIdsFromPasses(passes);
    final List<UserGroup> userGroups = getUserGroups(userGroupIds);

    long addedPassCount = addPasses(passes, userGroups);

    log.info("AddPassesTasklet - execute: 이용권 {}건 추가 완료, startedAt={}", addedPassCount, startedAt);
    return RepeatStatus.FINISHED;
  }

  /**
   * 진행중인 이용권 조회
   *
   * @param status    이용권 상태
   * @param startedAt 시작 시간
   * @return          진행중인 이용권
   */
  private List<Pass> getNowPasses(PassStatus status, LocalDateTime startedAt) {
    return passRepository
        .findByPassStatusAndStartedAtLessThanAndExpiredAtGreaterThan(status, startedAt, startedAt);
  }

  private List<Integer> getUserGroupIdsFromPasses(List<Pass> passes) {
    return passes.stream()
        .map(Pass::getUserGroupId)
        .toList();
  }

  private List<UserGroup> getUserGroups(List<Integer> userGroupIds) {
    return userGroupRepository.findByUserGroupIdIn(userGroupIds);
  }

  /**
   * 이용권 지급
   *
   * @param passes     진행중인 이용권
   * @param userGroups 이용권 제공할 유저 그룹
   * @return           유저에게 지급한 이용권 수
   */
  private long addPasses(List<Pass> passes, List<UserGroup> userGroups) {
    List<UserPass> userPasses = new ArrayList<>();  // 유저에게 지급할 이용권
    for (Pass pass : passes) {
      List<Long> userIds = getUserIdsFromUserGroups(pass, userGroups);

      for (Long userId : userIds) {
        userPasses.add(UserPass.of(userId, pass));
      }

      pass.setPassStatus(PassStatus.COMPLETED);
    }

    passRepository.saveAll(passes);
    return userPassRepository.saveAll(userPasses).size();
  }

  /**
   * 유저 ID 획득
   *
   * @param pass 진행중인 이용권
   * @param userGroups 유저 그룹
   * @return 유저 그룹에서 진행중인 이용권에 해당하는 유저 ID
   */
  private List<Long> getUserIdsFromUserGroups(Pass pass, List<UserGroup> userGroups) {
    return userGroups.stream()
        .filter(u -> Objects.equals(u.getUserGroupId(), pass.getUserGroupId()))
        .map(UserGroup::getUserId)
        .toList();
  }
}
