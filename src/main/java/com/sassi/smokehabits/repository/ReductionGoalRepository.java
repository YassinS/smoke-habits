package com.sassi.smokehabits.repository;

import com.sassi.smokehabits.entity.ReductionGoal;
import com.sassi.smokehabits.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReductionGoalRepository extends JpaRepository<ReductionGoal, UUID> {

    List<ReductionGoal> findAllByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT rg FROM ReductionGoal rg WHERE rg.user = :user AND rg.status = 'ACTIVE' ORDER BY rg.createdAt DESC")
    Optional<ReductionGoal> findActiveGoalByUser(User user);

    @Query("SELECT rg FROM ReductionGoal rg WHERE rg.user.id = :userId AND rg.status = 'ACTIVE' ORDER BY rg.createdAt DESC")
    Optional<ReductionGoal> findActiveGoalByUserId(UUID userId);

    long countByUserAndStatus(User user, ReductionGoal.GoalStatus status);
}
