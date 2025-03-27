/*package com.example.demo.jobscheduler;

import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "jobs")*/
//public class Job {
//
//    @Setter
//    @Id
//    @GeneratedValue
//    private UUID id;
//
//    @Setter
//    @Column(name = "name", nullable = false, length = 255)
//    private String name;
//
//    @Column(name = "binary_type", nullable = false, length = 50)
//    private String binaryType; // JAR/NPM
//
//    @Column(name = "binary_path", nullable = false, length = 500)
//    private String binaryPath; // File path in MinIO
//
//    @Column(name = "schedule_type", nullable = false, length = 50)
//    private String scheduleType; // Immediate, One-Time, Recurring
//
//    @Column(name = "schedule_time")
//    private LocalDateTime scheduleTime; // Execution time for one-time jobs
//
//    @Column(name = "recurrence_pattern", length = 100)
//    private String recurrencePattern; // e.g., "*/10 * * * *" for every 10 minutes
//
//    @Setter
//    @Column(name = "timezone", nullable = false, length = 100)
//    private String timezone; // e.g., "UTC", "America/New_York"
//
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private LocalDateTime createdAt = LocalDateTime.now();
//
//    @Setter
//    @Column(name = "updated_at", nullable = false)
//    private LocalDateTime updatedAt = LocalDateTime.now();
//
//    // Constructors
//    public Job() {}
//
//    public Job(String name, String binaryType, String binaryPath, String scheduleType,
//               LocalDateTime scheduleTime, String recurrencePattern, String timezone) {
//        this.name = name;
//        this.binaryType = binaryType;
//        this.binaryPath = binaryPath;
//        this.scheduleType = scheduleType;
//        this.scheduleTime = scheduleTime;
//        this.recurrencePattern = recurrencePattern;
//        this.timezone = timezone;
//    }
//
//    // Getters and Setters
//    public UUID getId() {
//        return id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getBinaryType() {
//        return binaryType;
//    }
//
//    public void setBinaryType(String binaryType) {
//        this.binaryType = binaryType;
//    }
//
//    public String getBinaryPath() {
//        return binaryPath;
//    }
//
//    public void setBinaryPath(String binaryPath) {
//        this.binaryPath = binaryPath;
//    }
//
//    public String getScheduleType() {
//        return scheduleType;
//    }
//
//    public void setScheduleType(String scheduleType) {
//        this.scheduleType = scheduleType;
//    }
//
//    public LocalDateTime getScheduleTime() {
//        return scheduleTime;
//    }
//
//    public void setScheduleTime(LocalDateTime scheduleTime) {
//        this.scheduleTime = scheduleTime;
//    }
//
//    public String getRecurrencePattern() {
//        return recurrencePattern;
//    }
//
//    public void setRecurrencePattern(String recurrencePattern) {
//        this.recurrencePattern = recurrencePattern;
//    }
//
//    public String getTimezone() {
//        return timezone;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public LocalDateTime getUpdatedAt() {
//        return updatedAt;
//    }
//
//}

package com.example.demo.jobscheduler;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "jobs")

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    //private String minioFileUrl;

    @Column(name = "binary_type", nullable = false, length = 50)
    private String binaryType; // JAR/NPM

    @Column(name = "binary_path", length = 500)
    private String binaryPath; // File path in MinIO

    @Column(name = "schedule_type", nullable = false, length = 50)
    private String scheduleType; // Immediate, One-Time, Recurring

    @Column(name = "schedule_time")
    private LocalDateTime scheduleTime; // Execution time for one-time jobs

    @Column(name = "recurrence_pattern", length = 100)
    private String recurrencePattern; // e.g., "*/10 * * * *" for every 10 minutes

    @Column(name = "timezone", nullable = false, length = 100)
    private String timezone; // e.g., "UTC", "America/New_York"

    /*@Column(name = "message_body", columnDefinition = "TEXT")
    private String messageBody; // New field for storing message data*/

    @Column(name = "message_body", columnDefinition = "TEXT", nullable = true)
    private String messageBody;


    /*@Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // Stores client-provided message data*/

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /*public String getMinioFileUrl() { return minioFileUrl; }
    public void setMinioFileUrl(String minioFileUrl) { this.minioFileUrl = minioFileUrl; }*/

    // Constructors
    public Job() {}

    public Job(String name, String binaryType, String binaryPath, String scheduleType,
               LocalDateTime scheduleTime, String recurrencePattern, String timezone, String messageBody) {
        this.name = name;
        this.binaryType = binaryType;
        this.binaryPath = binaryPath;
        this.scheduleType = scheduleType;
        this.scheduleTime = scheduleTime;
        this.recurrencePattern = recurrencePattern;
        this.timezone = timezone;
        //this.metadata = metadata;
        this.messageBody = messageBody;
    }

    // Auto-set timestamps
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

//asd

