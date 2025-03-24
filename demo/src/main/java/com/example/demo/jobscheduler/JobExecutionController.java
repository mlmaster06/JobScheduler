package com.example.demo.jobscheduler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/job-executions")
public class JobExecutionController {

    private final JobExecutionService jobExecutionService;

    public JobExecutionController(JobExecutionService jobExecutionService) {
        this.jobExecutionService = jobExecutionService;
    }

    // 1️⃣ Get all job executions
    @GetMapping
    public ResponseEntity<List<JobExecution>> getAllExecutions() {
        return ResponseEntity.ok(jobExecutionService.getAllExecutions());
    }

    // 2️⃣ Get execution by ID
    @GetMapping("/{id}")
    public ResponseEntity<JobExecution> getExecutionById(@PathVariable UUID id) {
        return jobExecutionService.getExecutionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3️⃣ Get executions for a specific job
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<JobExecution>> getExecutionsByJobId(@PathVariable UUID jobId) {
        return ResponseEntity.ok(jobExecutionService.getExecutionsByJobId(jobId));
    }

    // 4️⃣ Start a new job execution
    @PostMapping("/start/{jobId}")
    public ResponseEntity<JobExecution> startJobExecution(@PathVariable UUID jobId) {
        return ResponseEntity.ok(jobExecutionService.startJobExecution(jobId));
    }

    // 5️⃣ Mark a job execution as completed
    @PutMapping("/{executionId}/complete")
    public ResponseEntity<JobExecution> completeJobExecution(
            @PathVariable UUID executionId,
            @RequestParam boolean success,
            @RequestParam(required = false) String logs,
            @RequestParam(required = false) String errorMessage) {
        return ResponseEntity.ok(jobExecutionService.completeJobExecution(executionId, success, logs, errorMessage));
    }

    // 6️⃣ Retry a failed job execution
    @PostMapping("/{id}/retry")
    public ResponseEntity<String> retryFailedJob(@PathVariable UUID id) {
        boolean retried = jobExecutionService.retryFailedJob(id);
        return retried ? ResponseEntity.ok("Job retried successfully") : ResponseEntity.badRequest().body("Retry failed");
    }
}

