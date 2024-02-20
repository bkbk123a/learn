package com.example.batch.repository.pass;

import com.example.batch.entity.pass.UserGroup;
import com.example.batch.entity.pass.UserGroupId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupId> {

    List<UserGroup> findByUserGroupIdIn(List<Integer> userGroupIds);
}
