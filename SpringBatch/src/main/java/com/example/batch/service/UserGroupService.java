package com.example.batch.service;

import com.example.batch.entity.pass.UserGroup;
import com.example.batch.repository.pass.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserGroupService {

  private final UserGroupRepository userGroupRepository;
  public List<UserGroup> getUserGroups(List<Integer> userGroupIds) {
    return userGroupRepository.findByUserGroupIdIn(userGroupIds);
  }
}