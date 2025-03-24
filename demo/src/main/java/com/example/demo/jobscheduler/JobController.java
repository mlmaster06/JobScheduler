package com.example.demo.jobscheduler;

//import com.example.demo.jobscheduler.models.Job;
import com.example.demo.jobscheduler.Job;
import com.example.demo.jobscheduler.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@RestController
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

    /*@PostMapping
    public ResponseEntity<Job> createJob(@RequestBody Job job) {
        return ResponseEntity.ok(jobService.createJob(job));
    }*/
    /*@PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Job> createJob(@RequestPart("job") Job job, @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            return ResponseEntity.ok(jobService.createJob(job, file));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }*/

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Job> createJob(@RequestPart("job") Job job,
                                         @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            return ResponseEntity.ok(jobService.createJob(job, file));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
    /*@PutMapping("/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable UUID id, @RequestBody Job job) {
        return ResponseEntity.ok(jobService.updateJob(id, job));
    }*/
    /*@PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Job> updateJob(@PathVariable UUID id, @RequestPart("job") Job job, @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            return ResponseEntity.ok(jobService.updateJob(id, job, file));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }*/
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
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = minioService.uploadFile(file);
            return ResponseEntity.ok("File uploaded successfully: " + fileUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }

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
}
