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

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // 이용권 시작 1일 전 user group 내 각 사용자들에게 이용권 추가.
        final LocalDateTime startedAt = LocalDateTime.now().minusDays(1);
        final List<Pass> passes = getNowPasses(PassStatus.READY, startedAt);
        final List<UserGroup> userGroups = getUserGroups(getUserGroupIdsFromPasses(passes));

        long addedPassCount = addPasses(passes, userGroups);

        log.info("AddPassesTasklet - execute: 이용권 {}건 추가 완료, startedAt={}", addedPassCount, startedAt);
        return RepeatStatus.FINISHED;
    }

    private List<Pass> getNowPasses(PassStatus status, LocalDateTime startedAt) {
        return passRepository
                .findByPassStatusAndStartedAtGreaterThanAndExpiredAtLessThan(status, startedAt, startedAt);
    }

    private List<Integer> getUserGroupIdsFromPasses(List<Pass> passes) {
        return passes.stream()
                .map(Pass::getUserGroupId)
                .toList();
    }

    private List<UserGroup> getUserGroups(List<Integer> userGroupIds) {
        return userGroupRepository.findByUserGroupIdIn(userGroupIds);
    }

    private long addPasses(List<Pass> passes, List<UserGroup> userGroups) {
        List<UserPass> userPasses = new ArrayList<>();
        for (Pass pass : passes) {
            List<Long> userIds = getUserIdsFromUserGroups(pass, userGroups);

            for(Long userId : userIds) {
                userPasses.add(UserPass.of(userId, pass));
            }
        }

        passes.forEach(p -> p.setPassStatus(PassStatus.COMPLETED));
        passRepository.saveAll(passes);
        return userPassRepository.saveAll(userPasses).size();
    }

    private List<Long> getUserIdsFromUserGroups(Pass pass, List<UserGroup> userGroups) {
        return userGroups.stream()
                .filter(u-> Objects.equals(u.getUserGroupId(), pass.getUserGroupId()))
                .map(UserGroup::getUserId)
                .toList();
    }
}
