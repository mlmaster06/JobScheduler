package com.example.demo.config;

import com.example.demo.jobscheduler.MinioService;
import lombok.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;


@Component
public class KafkaListeners {

    private final SimpMessagingTemplate messagingTemplate;


    private final MinioService minioService;

    @Autowired
    public KafkaListeners(SimpMessagingTemplate messagingTemplate,MinioService minioService) {
        this.messagingTemplate = messagingTemplate;
        this.minioService = minioService;
    }

    @KafkaListener(
            topics = "JobSchedulerTopic",
            //groupId = "job-scheduler-group"
            groupId = "#{@environment.getProperty('spring.kafka.consumer.group-id')}"

    )
    public void listenAndExecute(String message) {
        try {
            JSONObject kafkaMessage = new JSONObject(message);
            String messageBody = kafkaMessage.optString("messageBody", "No message body found");
            String binaryPath = kafkaMessage.optString("binaryPath", "");

            System.out.println("Received from Kafka: " + messageBody);
            System.out.println("Binary to execute: " + binaryPath);

            //String localFilePath = minioService.downloadFile(binaryPath);
            String localPath = minioService.downloadFile(binaryPath);

            // Execute the JAR/NPM binary
            executeBinary(localPath);

        } catch (Exception e) {
            System.err.println("Error processing Kafka message: " + e.getMessage());
        }
    }

   /* private String downloadFile(String binaryPath) {
        try {
            // ✅ If the binary path is a remote MinIO URL, download it
            if (binaryPath.startsWith("http")) {
                String localPath = minioService.downloadFile(binaryPath);
                System.out.println("Binary downloaded to: " + localPath);
                return localPath;
            }
            // If it's already a local path, return it as is
            return binaryPath;
        } catch (Exception e) {
            System.err.println("Error downloading file from MinIO: " + e.getMessage());
            return null;
        }
    }*/


    private void executeBinary(String binaryPath) {
        try {
            if (binaryPath.isEmpty()) {
                System.out.println("No binary path provided.");
                return;
            }

            File binaryFile = new File(binaryPath);
            if (!binaryFile.exists()) {
                System.err.println("Binary file not found: " + binaryPath);
                return;
            }

            Process process;
            if (binaryPath.endsWith(".jar")) {
                process = new ProcessBuilder("java", "-jar", binaryPath).start();
            } else if (binaryPath.endsWith(".npm")) {
                process = new ProcessBuilder("npm", "install", binaryPath).start();
            } else {
                System.out.println("Unsupported binary type.");
                return;
            }

            process.waitFor();
            System.out.println("Binary executed successfully: " + binaryPath);

        } catch (Exception e) {
            System.err.println("Error executing binary: " + e.getMessage());
        }



            /*ProcessBuilder processBuilder;

            if (binaryPath.endsWith(".jar")) {
                processBuilder = new ProcessBuilder("java", "-jar", binaryPath);
            } else if (binaryPath.endsWith(".npm")) {
                processBuilder = new ProcessBuilder("npm", "install", binaryPath);
            } else {
                System.out.println("Unsupported binary type.");
                return;
            }

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Wait for execution to complete
            int exitCode = process.waitFor();
            System.out.println("Binary execution completed with exit code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing binary: " + e.getMessage());
        }*/
    }
}

//asd