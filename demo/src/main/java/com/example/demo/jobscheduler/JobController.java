package com.example.demo.jobscheduler;

//import com.example.demo.jobscheduler.models.Job;
import com.example.demo.jobscheduler.Job;
import com.example.demo.jobscheduler.JobService;
import jakarta.validation.Valid;
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

/*@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    private final MinioService minioService;

    @Autowired
    public JobController(JobService jobService,MinioService minioService) {
        this.jobService = jobService;
        this.minioService = minioService;
    }


    // 1️⃣ Create a new Job with file Upload

    *//*@PostMapping
    public ResponseEntity<Job> createJob(@RequestBody Job job) {
        return ResponseEntity.ok(jobService.createJob(job));
    }*//*
    *//*@PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Job> createJob(@RequestPart("job") Job job, @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            return ResponseEntity.ok(jobService.createJob(job, file));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }*//*

    *//*@PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Job> createJob(@RequestPart("job") Job job,
                                         @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            return ResponseEntity.ok(jobService.createJob(job, file));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }*//*

    @PostMapping(consumes = {"multipart/form-data", "application/json"})
    public ResponseEntity<?> createJob(
            @Valid @RequestPart("job") Job job,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        try {
            Job createdJob = jobService.createJob(job, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdJob);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create job", "details", e.getMessage()));
        }
    }

    // Global exception handler for validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }




    // 2️⃣ Get all Jobs
    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    // 3️⃣ Get a Job by ID
    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    // 4️⃣ Update a Job with File Upload
    *//*@PutMapping("/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable UUID id, @RequestBody Job job) {
        return ResponseEntity.ok(jobService.updateJob(id, job));
    }*//*
    *//*@PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Job> updateJob(@PathVariable UUID id, @RequestPart("job") Job job, @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            return ResponseEntity.ok(jobService.updateJob(id, job, file));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }*//*
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Job> updateJob(@PathVariable UUID id,
                                         @RequestPart("job") Job job,
                                         @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            return ResponseEntity.ok(jobService.updateJob(id, job, file));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 5️⃣ Delete a Job
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable UUID id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    // 6️⃣ Upload file to MinIO
   *//* @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = minioService.uploadFile(file);
            return ResponseEntity.ok("File uploaded successfully: " + fileUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }*//*

    // 7️⃣ Download file from MinIO
    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        try {
            InputStream inputStream = minioService.downloadFile(fileName);
            byte[] bytes = inputStream.readAllBytes();
            return ResponseEntity.ok().body(bytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}*/

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

