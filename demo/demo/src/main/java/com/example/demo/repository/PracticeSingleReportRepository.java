package com.example.demo.repository;

import com.example.demo.entity.PracticeSingleReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PracticeSingleReportRepository extends JpaRepository<PracticeSingleReport, Long> {
    Optional<PracticeSingleReport> findBySingleIdAndIsDelete(Long singleId, Integer isDelete);
}