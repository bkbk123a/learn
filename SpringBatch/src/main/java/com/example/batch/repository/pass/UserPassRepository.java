package com.example.batch.repository.pass;

import com.example.batch.entity.pass.UserPass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPassRepository extends JpaRepository<UserPass, Integer> {
}
