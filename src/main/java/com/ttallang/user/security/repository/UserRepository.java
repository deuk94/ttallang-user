package com.ttallang.user.security.repository;

import com.ttallang.user.commonModel.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUserId(int userId);
}