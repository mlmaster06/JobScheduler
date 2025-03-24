package com.example.demo.jobscheduler;  // Ensure correct package

import com.example.demo.jobscheduler.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
}

