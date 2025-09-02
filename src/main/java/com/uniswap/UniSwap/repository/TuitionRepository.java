package com.uniswap.UniSwap.repository;

import com.uniswap.UniSwap.entity.Tuition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TuitionRepository extends JpaRepository<Tuition, Integer> {
    List<Tuition> findByPostPostId(Integer postId);
    List<Tuition> findByLocationLocationId(Integer locationId);
    List<Tuition> findBySubject(String subject);
    @Query("SELECT t FROM Tuition t WHERE t.tStatus = :status")
    List<Tuition> findByTStatus(@Param("status") String status);
    
    @Query("SELECT t FROM Tuition t WHERE t.salary <= :maxSalary")
    List<Tuition> findBySalaryLessThanEqual(Integer maxSalary);
}