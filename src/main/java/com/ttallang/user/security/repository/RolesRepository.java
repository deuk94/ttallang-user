package com.ttallang.user.security.repository;

import com.ttallang.user.commonModel.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

//@Repository 상속 없이 ioc 가능 JpaRepository 상속해서 자동 bean 등록
public interface RolesRepository extends JpaRepository<Roles, Integer> {

//    @Query("")
//    User findByUserId
    Roles findByUserName(String userName);
    void deleteByUserName(String userName);
}
