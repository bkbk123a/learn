package com.example.batch.repository.pass;

import com.example.batch.entity.pass.Pass;
import com.example.batch.enumerator.ProvidePassStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PassRepository extends JpaRepository<Pass, Integer> {

    // startAt < now < expireAt 조회
    List<Pass> findByPassStatusAndStartedAtLessThanAndExpiredAtGreaterThan(
        ProvidePassStatus status, LocalDateTime now1, LocalDateTime now2);
}
