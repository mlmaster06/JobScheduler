package com.example.demo.jobscheduler;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "scheduled_messages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledMessage {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "message_body", nullable = false)
    private String messageBody;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // JSONB stored as String in Java

    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;

    @Column(name = "status", nullable = false)
    private String status = "PENDING"; // Default status

    @Column(name = "kafka_topic", nullable = false)
    private String kafkaTopic;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}

//asd