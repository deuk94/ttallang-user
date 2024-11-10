package com.ttallang.user.commomRepository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ttallang.user.commonModel.Branch;
import org.springframework.data.jpa.repository.Query;


public interface BranchRepository extends JpaRepository<Branch, Integer> {

    // branch_status가 1인 대여소만 조회하는 쿼리 추가
    @Query("SELECT b FROM Branch b WHERE b.branchStatus = '1'")
    List<Branch> findActiveBranches();

}