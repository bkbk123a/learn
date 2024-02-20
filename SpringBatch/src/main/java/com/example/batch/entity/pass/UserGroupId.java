package com.example.batch.entity.pass;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserGroupId implements Serializable {

    private Integer userGroupId;
    private Long userId;
}
