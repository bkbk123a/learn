package com.example.batch.repository.pass;

import com.example.batch.entity.pass.UserPass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPassRepository extends JpaRepository<UserPass, Integer> {

  // 인자로 넘어온 UserId에 해당 하는 row 삭제
  void deleteAllByUserIdIn(List<Long> UserIds);
}
