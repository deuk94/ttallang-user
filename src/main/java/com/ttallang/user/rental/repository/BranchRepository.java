package com.ttallang.user.rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ttallang.user.commonModel.Branch;

public interface BranchRepository extends JpaRepository<Branch, Integer> {
}
