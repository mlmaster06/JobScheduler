package com.example.demo.jobscheduler;

import com.example.demo.config.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledJob {

    @Autowired
    private ScheduledMessageService messageService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private MinioService minioService;


    @Scheduled(fixedRate = 5000) // Every 5 seconds
    public void runJob() {
        System.out.println("Scheduled job running: " + System.currentTimeMillis());
        List<ScheduledMessage> messages = messageService.getPendingMessagesBefore(LocalDateTime.now());

        for (ScheduledMessage msg : messages) {
            try {

                // Extract fileUrl from metadata
                String binaryPath = extractBinaryPath(msg.getMetadata());

                // Create JSON object with messageBody & fileUrl
                /*JSONObject kafkaMessage = new JSONObject();
                kafkaMessage.put("messageBody", msg.getMessageBody());
                kafkaMessage.put("fileUrl", fileUrl);*/

                // ✅ Create JSON object with messageBody & binaryPath
                JSONObject kafkaMessage = new JSONObject();
                kafkaMessage.put("messageBody", msg.getMessageBody());
                kafkaMessage.put("binaryPath", binaryPath);

                System.out.println(msg.getMessageBody());

                // Send JSON to Kafka
                //kafkaProducerService.sendMessage("JobSchedulerTopic", msg.getMessageBody());  // Use KafkaProducerService
                kafkaProducerService.sendMessage("JobSchedulerTopic", msg.getMessageBody(), binaryPath);
                //kafkaProducerService.sendMessage("JobSchedulerTopic", kafkaMessage.toString());


                messageService.updateMessageStatus(msg.getId(), "SENT");
                System.out.println("Message sent to Kafka: " + msg.getMessageBody());
            } catch (Exception e) {
                messageService.updateMessageStatus(msg.getId(), "FAILED");
                System.err.println("Error sending message to Kafka: " + e.getMessage());
            }
        }
    }

    /*private String extractBinaryPath(String metadataJson) {
        if (metadataJson == null || metadataJson.isEmpty()) {
            return "";
        }
        try {
            JSONObject metadata = new JSONObject(metadataJson);
            return metadata.optString("binaryPath", ""); // Default to empty string if key is missing
        } catch (Exception e) {
            System.err.println("Error parsing metadata JSON: " + e.getMessage());
            return "";
        }
    }
*/

    private String extractBinaryPath(String metadataJson) {
        if (metadataJson == null || metadataJson.isEmpty()) {
            return "";
        }

        try {
            JSONObject metadata = new JSONObject(metadataJson);
            String binaryPath = metadata.optString("binaryPath", "");

            // Check if binaryPath itself is a JSON string containing binary_path
            if (binaryPath.startsWith("{") && binaryPath.endsWith("}")) {
                try {
                    JSONObject nestedJson = new JSONObject(binaryPath);
                    return nestedJson.optString("binary_path", binaryPath);
                } catch (Exception e) {
                    // If it's not valid JSON, just return the binaryPath as is
                    return binaryPath;
                }
            }

            return binaryPath;
        } catch (Exception e) {
            System.err.println("Error parsing metadata JSON: " + e.getMessage());

            // Fallback: try to extract URL directly if the JSON parsing fails
            if (metadataJson.contains("http://")) {
                int startIndex = metadataJson.indexOf("http://");
                int endIndex = metadataJson.indexOf("\"", startIndex);
                if (startIndex >= 0 && endIndex > startIndex) {
                    return metadataJson.substring(startIndex, endIndex);
                }
            }

            return "";
        }
    }

}
