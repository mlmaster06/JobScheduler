package com.example.demo.jobscheduler;

//import com.example.demo.jobscheduler.models.Job;
import com.example.demo.jobscheduler.Job;
import com.example.demo.jobscheduler.JobService;
import jakarta.validation.Valid;
import com.example.demo.jobscheduler.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.RequestPart;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;
    private final MinioService minioService;
    private final ObjectMapper objectMapper;

    @Autowired
    public JobController(JobService jobService, MinioService minioService, ObjectMapper objectMapper) {
        this.jobService = jobService;
        this.minioService = minioService;
        this.objectMapper = objectMapper;
    }

    // ✅ Create Job with multipart request
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> createJob(
            @RequestPart("job") String jobJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            // Convert JSON string to Job object
            //ObjectMapper objectMapper = new ObjectMapper();
            Job job = objectMapper.readValue(jobJson, Job.class);

            // Create job
            Job createdJob = jobService.createJob(job, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdJob);

        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid JSON format", "details", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Job creation failed", "details", e.getMessage()));
        }

        /*catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid input", "details", e.getMessage()));
        }*/

    }

    // ✅ Get all Jobs
    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    // ✅ Get a Job by ID
    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    // ✅ Update a Job with File Upload
   /* @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Job> updateJob(@PathVariable UUID id,
                                         @RequestPart("job") String jobJson,
                                         @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            // Convert JSON string to Job object
            Job job = objectMapper.readValue(jobJson, Job.class);

            return ResponseEntity.ok(jobService.updateJob(id, job, file));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // ✅ Delete a Job
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable UUID id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }*/

    // ✅ Download file from MinIO
    /*@GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        try {
            // ✅ Get actual file bytes from MinIO
            InputStream inputStream = minioService.downloadFile(fileName);
            byte[] fileBytes = inputStream.readAllBytes();
            return ResponseEntity.ok().body(fileBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }


        *//*try {
            byte[] fileBytes = minioService.downloadFile(fileName);
            return ResponseEntity.ok().body(fileBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }*//*
    }*/




}




//asddghjhvhvvjgvgj