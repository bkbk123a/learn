package com.example.batch.service;

import com.example.batch.entity.pass.Pass;
import com.example.batch.entity.pass.UserGroup;
import com.example.batch.entity.pass.UserPass;
import com.example.batch.enumerator.ProvidePassStatus;
import com.example.batch.repository.pass.PassRepository;
import com.example.batch.repository.pass.UserPassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PassService {
  private final PassRepository passRepository;
  private final UserPassRepository userPassRepository;

  /**
   * 진행중인 이용권 조회
   *
   * @param status    이용권 상태
   * @param startedAt 시작 시간
   * @return          진행중인 이용권
   */
  public List<Pass> getNowPasses(ProvidePassStatus status, LocalDateTime startedAt) {
    return passRepository
        .findByPassStatusAndStartedAtLessThanAndExpiredAtGreaterThan(status, startedAt, startedAt);
  }

  public List<Integer> getUserGroupIdsFromPasses(List<Pass> passes) {
    return passes.stream()
        .map(Pass::getUserGroupId)
        .toList();
  }

  /**
   * 이용권 지급
   *
   * @param passes     진행중인 이용권
   * @param userGroups 이용권 제공할 유저 그룹
   * @return           유저에게 지급한 이용권 수
   */
  public long addPasses(List<Pass> passes, List<UserGroup> userGroups) {
    List<UserPass> userPasses = new ArrayList<>();  // 유저에게 지급할 이용권
    for (Pass pass : passes) {
      List<Long> userIds = getUserIdsFromUserGroups(pass, userGroups);

      for (Long userId : userIds) {
        userPasses.add(UserPass.of(userId, pass));  // 이용권 지급 후 유저의 이용권 상태는 Ready
      }
      // 이용권 지급 완료
      pass.setPassStatus(ProvidePassStatus.COMPLETED);
    }

    passRepository.saveAll(passes);
    return userPassRepository.saveAll(userPasses).size();
  }

  /**
   * 이용권(pass)의 groupId와 일치 하는 유저 Id 획득
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
