package com.example.demo.jobscheduler;

import com.example.demo.jobscheduler.Job;
import com.example.demo.jobscheduler.JobRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.io.*;
import java.nio.file.Path;  // ✅ Correct import
import java.nio.file.Paths; // ✅ Correct import
import java.util.UUID;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final ScheduledMessageService scheduledMessageService;
    private final MinioService minioService;

    @Autowired
    public JobService(JobRepository jobRepository,ScheduledMessageService scheduledMessageService,MinioService minioService) {
        this.jobRepository = jobRepository;
        //this.scheduledMessageRepository = scheduledMessageRepository;
        this.scheduledMessageService = scheduledMessageService;
        this.minioService = minioService;

    }


    @Transactional
    public Job createJob(Job job, MultipartFile file) throws Exception {
        if (file != null && !file.isEmpty()) {
            // ✅ Upload file to MinIO and get file URL
            String fileUrl = minioService.uploadFile(file);
            job.setBinaryPath(fileUrl);
        }

        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        Job savedJob = jobRepository.save(job);

        // ✅ Construct metadata JSON with binary_path
        String metadataJson = "{\"binary_path\": \"" + job.getBinaryPath() + "\"}";

        // ✅ Create scheduled message with metadata
        ScheduledMessage scheduledMessage = ScheduledMessage.builder()
                .messageBody(job.getMessageBody())
                .metadata(metadataJson)
                .scheduledTime(job.getScheduleTime() != null ? job.getScheduleTime() : LocalDateTime.now())
                .status("PENDING")
                .kafkaTopic("JobSchedulerTopic")
                .build();

        scheduledMessageService.scheduleMessage(scheduledMessage);
        return savedJob;
    }


    // Get all jobs
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    // Get a job by ID
    public Job getJobById(UUID id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + id));
    }


    // Update job
    /*@Transactional
    public Job updateJob(UUID id, Job job) {
        Job existingJob = getJobById(id);

        existingJob.setBinaryType(job.getBinaryType());
        existingJob.setBinaryPath(job.getBinaryPath());
        existingJob.setScheduleType(job.getScheduleType());
        existingJob.setScheduleTime(job.getScheduleTime());
        existingJob.setRecurrencePattern(job.getRecurrencePattern());
        existingJob.setTimezone(job.getTimezone());
        //existingJob.setMetadata(job.getMetadata());
        existingJob.setUpdatedAt(LocalDateTime.now());
        existingJob.setMessageBody(job.getMessageBody());

        //return jobRepository.save(existingJob);

        Job updatedJob = jobRepository.save(existingJob);

        // Update or create scheduled message
        ScheduledMessage scheduledMessage = scheduledMessageService.findByScheduledTime(existingJob.getScheduleTime())
                .orElse(ScheduledMessage.builder()
                        .messageBody(existingJob.getMessageBody())
                        .metadata("{}")
                        .scheduledTime(existingJob.getScheduleTime() != null ? existingJob.getScheduleTime() : LocalDateTime.now())
                        .status("PENDING")
                        .kafkaTopic("default-topic")
                        .build());

        scheduledMessageService.scheduleMessage(scheduledMessage);

        return updatedJob;
    }*/

    /*@Transactional
    public Job updateJob(UUID id, Job job) {
        Job existingJob = getJobById(id);

        existingJob.setBinaryType(job.getBinaryType());
        existingJob.setBinaryPath(job.getBinaryPath());
        existingJob.setScheduleType(job.getScheduleType());
        existingJob.setScheduleTime(job.getScheduleTime());
        existingJob.setRecurrencePattern(job.getRecurrencePattern());
        existingJob.setTimezone(job.getTimezone());
        existingJob.setUpdatedAt(LocalDateTime.now());
        existingJob.setMessageBody(job.getMessageBody());

        Job updatedJob = jobRepository.save(existingJob);

        // Construct updated metadata JSON with binary_path
        String binaryPath = existingJob.getBinaryPath();
        String metadataJson = "{\"binary_path\": \"" + binaryPath + "\"}";

        // Update or create scheduled message
        ScheduledMessage scheduledMessage = scheduledMessageService.findByScheduledTime(existingJob.getScheduleTime())
                .orElse(ScheduledMessage.builder()
                        .messageBody(existingJob.getMessageBody())
                        .metadata(metadataJson) // ✅ Update metadata with binary_path
                        .scheduledTime(existingJob.getScheduleTime() != null ? existingJob.getScheduleTime() : LocalDateTime.now())
                        .status("PENDING")
                        .kafkaTopic("JobSchedulerTopic")
                        .build());

        scheduledMessageService.scheduleMessage(scheduledMessage);

        return updatedJob;
    }*/

    @Transactional
    public Job updateJob(UUID id, Job job, MultipartFile file) throws Exception {
        Job existingJob = getJobById(id);

        // ✅ Upload new file if provided
        if (file != null && !file.isEmpty()) {
            String fileUrl = minioService.uploadFile(file);
            existingJob.setBinaryPath(fileUrl);
        } else {
            existingJob.setBinaryPath(job.getBinaryPath()); // Keep old binary_path if no new file
        }

        // ✅ Update other job fields
        existingJob.setBinaryType(job.getBinaryType());
        existingJob.setScheduleType(job.getScheduleType());
        existingJob.setScheduleTime(job.getScheduleTime());
        existingJob.setRecurrencePattern(job.getRecurrencePattern());
        existingJob.setTimezone(job.getTimezone());
        existingJob.setUpdatedAt(LocalDateTime.now());
        existingJob.setMessageBody(job.getMessageBody());

        Job updatedJob = jobRepository.save(existingJob);

        // ✅ Construct metadata JSON with updated binary_path
        String metadataJson = "{\"binary_path\": \"" + existingJob.getBinaryPath() + "\"}";

        // ✅ Update or create scheduled message
        ScheduledMessage scheduledMessage = scheduledMessageService.findByScheduledTime(existingJob.getScheduleTime())
                .orElse(ScheduledMessage.builder()
                        .messageBody(existingJob.getMessageBody())
                        .metadata(metadataJson)
                        .scheduledTime(existingJob.getScheduleTime() != null ? existingJob.getScheduleTime() : LocalDateTime.now())
                        .status("PENDING")
                        .kafkaTopic("JobSchedulerTopic")
                        .build());

        scheduledMessageService.scheduleMessage(scheduledMessage);

        return updatedJob;
    }



    // ✅ Delete a job
    @Transactional
    public void deleteJob(UUID id) {
        if (!jobRepository.existsById(id)) {
            throw new RuntimeException("Job not found with ID: " + id);
        }
        jobRepository.deleteById(id);
    }
}
