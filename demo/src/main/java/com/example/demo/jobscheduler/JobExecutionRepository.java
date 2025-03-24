package com.example.demo.jobscheduler;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobExecutionRepository extends JpaRepository<JobExecution, UUID> {

    // Get all executions for a specific job
    List<JobExecution> findByJobId(UUID jobId);

    // Find the latest execution for a job
    Optional<JobExecution> findTopByJobIdOrderByStartTimeDesc(UUID jobId);
}

