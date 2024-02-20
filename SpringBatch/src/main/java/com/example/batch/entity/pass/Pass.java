package com.example.batch.entity.pass;

import com.example.batch.enumerator.PassStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "T_Pass")
@Builder
public class Pass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer passId;

    @Column(nullable = false)
    private Integer userGroupId;

    @Enumerated(EnumType.STRING)
    private PassStatus passStatus;

    @Column(nullable = false)
    private Integer count;  // 입장권 개수

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime startedAt = LocalDateTime.MIN;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime expiredAt = LocalDateTime.MIN;
}
