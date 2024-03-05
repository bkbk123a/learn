package com.example.batch.job.pass;

import com.example.batch.entity.pass.Pass;
import com.example.batch.entity.pass.UserGroup;
import com.example.batch.enumerator.PassStatus;
import com.example.batch.enumerator.ProvidePassStatus;
import com.example.batch.config.job.tasklet.AddPassTasklet;
import com.example.batch.repository.pass.PassRepository;
import com.example.batch.repository.pass.UserGroupRepository;
import com.example.batch.service.PassService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Junit 5
public class AddPassTaskletTest {

  @Mock
  private StepContribution stepContribution;

  @Mock
  private ChunkContext chunkContext;

  @Mock
  private PassRepository passRepository;

  @Mock
  private PassService passService;

  @Mock
  private UserGroupRepository userGroupRepository;

  @InjectMocks
  private AddPassTasklet addPassTasklet;

  private final long START_USER_ID = 100000L;
  final int USER_GROUP_ID = 1;
  final int REMAIN_PASS_COUNT = 10;

  @Test
  public void test() {
    // when
    final LocalDateTime now = LocalDateTime.now();

    final Pass pass = getPass(now);
    final UserGroup userGroup = getUserGroup();

    when(passService.getNowPasses(ProvidePassStatus.READY, any()))
        .thenReturn(List.of(pass));
    when(userGroupRepository.findByUserGroupIdIn(List.of(USER_GROUP_ID)))
        .thenReturn(List.of(userGroup));

    RepeatStatus repeatStatus = addPassTasklet.execute(stepContribution, chunkContext);

    // then
    assertEquals(RepeatStatus.FINISHED, repeatStatus);

    // 추가된 Pass 값 확인한다.
    // 추가된 PassEntity 값을 확인합니다.
    ArgumentCaptor<List> passCaptor = ArgumentCaptor.forClass(List.class);
    verify(passRepository, times(1)).saveAll(passCaptor.capture());
    final List<Pass> passes = passCaptor.getValue();

    assertEquals(1, passes.size());
    final Pass jobPass = passes.get(0);
    assertEquals(USER_GROUP_ID, jobPass.getUserGroupId());
    assertEquals(PassStatus.READY, jobPass.getPassStatus());
    assertEquals(REMAIN_PASS_COUNT, jobPass.getCount());
  }

  private Pass getPass(LocalDateTime now) {
    return Pass.builder()
        .userGroupId(USER_GROUP_ID)
        .passStatus(ProvidePassStatus.READY)
        .count(REMAIN_PASS_COUNT)
        .startedAt(now)
        .expiredAt(now.plusDays(30))
        .build();
  }

  private UserGroup getUserGroup() {
    return UserGroup.builder()
        .userGroupId(USER_GROUP_ID)
        .userId(START_USER_ID)
        .build();
  }
}
