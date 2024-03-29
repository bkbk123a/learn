package com.example.batch.entity.pass;

import com.example.batch.entity.BaseEntity;
import com.example.batch.enumerator.PassStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "T_User_Pass")
@SuperBuilder
public class UserPass extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userPassId;

    // 패키지시퀀스에 대해 다시 하기
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer passId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PassStatus passStatus = PassStatus.READY;  // 가지고 있는 입장권 상태

    @Column(nullable = false)
    private Integer remainingCount; // 남은 입장권 개수

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime startedAt = LocalDateTime.MIN;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime expiredAt = LocalDateTime.MIN;

    public static UserPass of(long userId, Pass pass) {
        return UserPass.builder()
                .userId(userId)
                .passId(pass.getPassId())
                .remainingCount(pass.getCount())
                .startedAt(pass.getStartedAt())
                .expiredAt(pass.getExpiredAt())
                .build();
    }
}
