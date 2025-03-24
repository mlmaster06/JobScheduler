package com.example.demo.jobscheduler;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class JobExecutionService {

    private final JobExecutionRepository jobExecutionRepository;
    private final JobRepository jobRepository;  // To validate job existence

    public JobExecutionService(JobExecutionRepository jobExecutionRepository, JobRepository jobRepository) {
        this.jobExecutionRepository = jobExecutionRepository;
        this.jobRepository = jobRepository;
    }

    // 1️⃣ Fetch all executions
    public List<JobExecution> getAllExecutions() {
        return jobExecutionRepository.findAll();
    }

    // 2️⃣ Fetch execution by ID
    public Optional<JobExecution> getExecutionById(UUID id) {
        return jobExecutionRepository.findById(id);
    }

    // 3️⃣ Fetch executions for a specific job
    public List<JobExecution> getExecutionsByJobId(UUID jobId) {
        return jobExecutionRepository.findByJobId(jobId);
    }

    // 4️⃣ Create a new job execution entry when a job starts
    @Transactional
    public JobExecution startJobExecution(UUID jobId) {
        if (!jobRepository.existsById(jobId)) {
            throw new IllegalArgumentException("Job ID not found: " + jobId);
        }

        JobExecution execution = new JobExecution();
        execution.setJobId(jobId);
        execution.setStatus("RUNNING");
        execution.setStartTime(LocalDateTime.now());
        return jobExecutionRepository.save(execution);
    }

    // 5️⃣ Update job execution status when a job finishes
    @Transactional
    public JobExecution completeJobExecution(UUID executionId, boolean success, String logs, String errorMessage) {
        JobExecution execution = jobExecutionRepository.findById(executionId)
                .orElseThrow(() -> new NoSuchElementException("Execution ID not found: " + executionId));

        execution.setEndTime(LocalDateTime.now());
        execution.setStatus(success ? "SUCCESS" : "FAILURE");
        execution.setLogs(logs);
        execution.setErrorMessage(errorMessage);
        return jobExecutionRepository.save(execution);
    }

    // 6️⃣ Retry a failed job execution
    @Transactional
    public boolean retryFailedJob(UUID executionId) {
        JobExecution execution = jobExecutionRepository.findById(executionId)
                .orElseThrow(() -> new NoSuchElementException("Execution ID not found: " + executionId));

        if (!"FAILURE".equals(execution.getStatus())) {
            return false;  // Only failed jobs can be retried
        }

        // Start a new execution for the same job
        startJobExecution(execution.getJobId());
        return true;
    }
}

