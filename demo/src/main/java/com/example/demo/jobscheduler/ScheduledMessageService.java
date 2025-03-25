package com.example.demo.jobscheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduledMessageService {

    private final ScheduledMessageRepository repository;

    // ✅ Save a new scheduled message with messageBody and binaryPath
    public ScheduledMessage scheduleMessage(ScheduledMessage message) {
        message.setKafkaTopic("JobSchedulerTopic");

        // Ensure metadata includes binaryPath
        String metadataJson = "{" +
                "\"binaryPath\": \"" + (message.getMetadata() != null ? message.getMetadata() : "") + "\"" +
                "}";

        message.setMetadata(metadataJson);
        return repository.save(message);
    }

    // ✅ Find scheduled message by scheduledTime
    public Optional<ScheduledMessage> findByScheduledTime(LocalDateTime scheduledTime) {
        return repository.findByScheduledTime(scheduledTime);
    }

    // ✅ Get all pending messages before a given time
    public List<ScheduledMessage> getPendingMessagesBefore(LocalDateTime time) {
        return repository.findByStatusAndScheduledTimeBefore("PENDING", time);
    }

    // ✅ Update message status after sending to Kafka
    @Transactional
    public void updateMessageStatus(UUID id, String status) {
        Optional<ScheduledMessage> optionalMessage = repository.findById(id);
        if (optionalMessage.isPresent()) {
            ScheduledMessage msg = optionalMessage.get();
            msg.setStatus(status);
            repository.save(msg);
        } else {
            throw new RuntimeException("ScheduledMessage not found with ID: " + id);
        }
    }
}


