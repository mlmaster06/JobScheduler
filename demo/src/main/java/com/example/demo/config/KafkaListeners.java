package com.example.demo.config;


import lombok.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {

    private final SimpMessagingTemplate messagingTemplate;

    public KafkaListeners(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(
            topics = "JobSchedulerTopic",
            //groupId = "job-scheduler-group"
            groupId = "#{@environment.getProperty('spring.kafka.consumer.group-id')}"

    )
    void listener(String data){
        //System.out.println("Received Message: " + data);

        //messagingTemplate.convertAndSend("/topic/job-notifications", data);

        try {
            // Parse the JSON message received from Kafka
            JSONObject jsonMessage = new JSONObject(data);
            String messageBody = jsonMessage.optString("messageBody", "");
            String fileUrl = jsonMessage.optString("fileUrl", "");

            System.out.println("Received Kafka Message: " + data);

            // Send only the messageBody to the client via WebSocket
            messagingTemplate.convertAndSend("/topic/job-notifications", messageBody);

            // Execute the JAR file/npm package (Without sending it to client)
            executeJarOrNpm(fileUrl);

        } catch (Exception e) {
            System.err.println("Error processing Kafka message: " + e.getMessage());
        }
    }

    private void executeJarOrNpm(String fileUrl) {
        try {
            if (fileUrl.endsWith(".jar")) {
                // Execute JAR file
                ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", fileUrl);
                processBuilder.start();
                System.out.println("Executed JAR file: " + fileUrl);
            } else if (fileUrl.endsWith("package.json")) {
                // Execute npm package (assuming it's a Node.js project)
                ProcessBuilder processBuilder = new ProcessBuilder("npm", "start", "--prefix", fileUrl);
                processBuilder.start();
                System.out.println("Executed npm package: " + fileUrl);
            } else {
                System.out.println("Unsupported file type: " + fileUrl);
            }
        } catch (Exception e) {
            System.err.println("Error executing file: " + e.getMessage());
        }
    }
}
