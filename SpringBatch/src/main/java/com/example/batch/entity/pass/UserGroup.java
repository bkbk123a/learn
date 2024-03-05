package com.example.batch.entity.pass;

import com.example.batch.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@Table(name = "T_User_Group")
@IdClass(UserGroupId.class)
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGroup extends BaseEntity {

    @Id
    private Integer userGroupId;

    @Id
    private Long userId;

    @Column(nullable = false)
    private String userGroupName;
}
