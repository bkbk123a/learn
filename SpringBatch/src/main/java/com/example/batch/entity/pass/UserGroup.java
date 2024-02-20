package com.example.batch.entity.pass;

import com.example.batch.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "T_User_Group")
@IdClass(UserGroupId.class)
public class UserGroup extends BaseEntity {

    @Id
    private Integer userGroupId;

    @Id
    private Long userId;

    @Column(nullable = false)
    private String userGroupName;
}
