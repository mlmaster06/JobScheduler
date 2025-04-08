/*
package com.example.demo.jobscheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

*/
/*@Service
@RequiredArgsConstructor
public class ScheduledMessageService {

    private final ScheduledMessageRepository repository;

    // Save a new scheduled message
    public ScheduledMessage scheduleMessage(ScheduledMessage message) {
        message.setKafkaTopic("JobSchedulerTopic");
        return repository.save(message);
    }

    // ✅ Find scheduled message by scheduledTime
    public Optional<ScheduledMessage> findByScheduledTime(LocalDateTime scheduledTime) {
        return repository.findByScheduledTime(scheduledTime);
    }



    // Get all pending messages before a given time
    public List<ScheduledMessage> getPendingMessagesBefore(LocalDateTime time) {
        return repository.findByStatusAndScheduledTimeBefore("PENDING", time);
    }

    // 3️⃣ Update message status after sending to Kafka
    *//*
*/
/*public void updateMessageStatus(UUID id, String status) {
        repository.findById(id).ifPresent(msg -> {
            msg.setStatus(status);
            repository.save(msg);
        });
    }*//*
*/
/*
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
}*//*


@Service
@RequiredArgsConstructor
public class ScheduledMessageService {

    private final ScheduledMessageRepository repository;


    public ScheduledMessage scheduleMessage(ScheduledMessage message) {
        message.setKafkaTopic("JobSchedulerTopic");

        // Check if the metadata is already a JSON string
        String metadataJson;
        try {
            // Try to parse it to see if it's valid JSON
            new JSONObject(message.getMetadata());
            // If it is valid JSON, use it directly
            metadataJson = message.getMetadata();
        } catch (Exception e) {
            // If it's not valid JSON or is null, create a simple JSON with binaryPath
            metadataJson = "{\"binaryPath\": \"" +
                    (message.getMetadata() != null ? message.getMetadata().replace("\"", "\\\"") : "") +
                    "\"}";
        }

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

//asd*/

package com.example.demo.jobscheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduledMessageService {

    private final ScheduledMessageRepository repository;


    public ScheduledMessage scheduleMessage(ScheduledMessage message) {
        message.setKafkaTopic("JobSchedulerTopic");

        // Ensure we have valid metadata that contains a direct binaryPath
        String metadataValue = message.getMetadata();
        String binaryPath = null;

        // First, determine if the metadata is already valid JSON with binaryPath
        if (metadataValue != null && !metadataValue.trim().isEmpty()) {
            try {
                // Check if it's JSON
                JSONObject json = new JSONObject(metadataValue);

                // If it has binaryPath, extract it
                if (json.has("binaryPath")) {
                    binaryPath = json.getString("binaryPath");
                } else {
                    // Assume the whole metadata is a direct path
                    binaryPath = metadataValue;
                }
            } catch (JSONException e) {
                // Not valid JSON, assume it's a direct path
                binaryPath = metadataValue;
            }
        }

        // Ensure we have a binary path
        if (binaryPath == null) {
            binaryPath = "";
        }

        // Create a clean message with binaryPath directly accessible
        try {
            JSONObject messageObject = new JSONObject();
            messageObject.put("messageBody", "Job scheduled for execution");
            messageObject.put("binaryPath", binaryPath);

            // Create a simple metadata JSON containing the direct binaryPath
            JSONObject metadataJson = new JSONObject();
            metadataJson.put("binaryPath", binaryPath);
            message.setMetadata(metadataJson.toString());
        } catch (JSONException e) {
            // Fallback in case of JSON errors
            message.setMetadata("{\"binaryPath\": \"" + binaryPath.replace("\"", "\\\"") + "\"}");
        }

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