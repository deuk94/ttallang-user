package com.ttallang.user.commomRepository;

import com.ttallang.user.commonModel.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepository extends JpaRepository<Roles, Integer> {

    Roles findByUserName(String userName);
}
