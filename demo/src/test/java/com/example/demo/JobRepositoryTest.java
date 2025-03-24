package com.example.demo;


//package com.example.demo.tests;

//package com.example.demo.jobscheduler;

import com.example.demo.jobscheduler.Job;
import com.example.demo.jobscheduler.JobRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class JobRepositoryTest {

    @Autowired
    private JobRepository jobRepository;

    @Test
    public void testJobEntityPersistence() {
        // Given: Create a Job entity
        Job job = Job.builder()
                .id(UUID.randomUUID())
                .name("Test Job")
                .binaryType("JAR")
                .binaryPath("s3://test-bucket/job.jar")
                .scheduleType("One-Time")
                .scheduleTime(LocalDateTime.now())
                .timezone("UTC")
                .messageBody("Test message")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When: Save it in the database
        Job savedJob = jobRepository.save(job);

        // Then: Verify that the Job is stored in the database
        assertThat(savedJob).isNotNull();
        assertThat(savedJob.getId()).isNotNull();
        assertThat(savedJob.getName()).isEqualTo("Test Job");
    }
}

