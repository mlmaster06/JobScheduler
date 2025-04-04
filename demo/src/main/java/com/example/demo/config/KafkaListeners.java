package com.example.demo.config;

import com.example.demo.jobscheduler.MinioService;
import com.example.demo.jobscheduler.WebSocketController;
import lombok.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;


@Component
public class KafkaListeners {

    private final SimpMessagingTemplate messagingTemplate;


    private final MinioService minioService;
    private final WebSocketController webSocketController;

    @Autowired
    public KafkaListeners(SimpMessagingTemplate messagingTemplate,MinioService minioService, WebSocketController webSocketController) {
        this.messagingTemplate = messagingTemplate;
        this.minioService = minioService;
        this.webSocketController = webSocketController;
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

            //messagingTemplate.convertAndSend("/topic/job-notifications", messageBody);
            webSocketController.sendNotification(messageBody);
            //System.out.println("Message Sent to WebSocket: " + messageBody);
            //String localFilePath = minioService.downloadFile(binaryPath);
            String localPath = minioService.downloadFile(binaryPath);

            // Execute the JAR/NPM binary
            executeBinary(localPath);

        } catch (Exception e) {
            System.err.println("Error processing Kafka message: " + e.getMessage());
        }
    }


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

            ProcessBuilder processBuilder;
            if (binaryPath.endsWith(".jar")) {
                processBuilder = new ProcessBuilder("java", "-jar", binaryPath);
            } else if (binaryPath.endsWith(".npm")) {
                processBuilder = new ProcessBuilder("npm", "install", binaryPath);
            } else {
                System.out.println("Unsupported binary type.");
                return;
            }

            // Create unique log files for this execution
            String fileName = new File(binaryPath).getName();
            String timestamp = String.valueOf(System.currentTimeMillis());

            // Create logs directory if it doesn't exist
            File logsDir = new File("logs");
            if (!logsDir.exists()) {
                logsDir.mkdirs();
            }

            File outputLogFile = new File(logsDir, "binary-" + fileName + "-" + timestamp + ".log");

            // Redirect process output and error to this file
            processBuilder.redirectOutput(ProcessBuilder.Redirect.to(outputLogFile));
            processBuilder.redirectError(ProcessBuilder.Redirect.to(outputLogFile));

            System.out.println("Starting execution of: " + binaryPath);
            System.out.println("Logging output to: " + outputLogFile.getAbsolutePath());

            // Start the process
            Process process = processBuilder.start();

            // Wait for the process to complete with timeout
            boolean completed = process.waitFor(5, TimeUnit.MINUTES); // Adjust timeout as needed

            if (completed) {
                int exitCode = process.exitValue();
                if (exitCode == 0) {
                    System.out.println("Binary executed successfully: " + binaryPath);
                    System.out.println("Check logs at: " + outputLogFile.getAbsolutePath());
                } else {
                    System.err.println("Binary execution failed with exit code: " + exitCode);
                    System.err.println("Check error logs at: " + outputLogFile.getAbsolutePath());
                }
            } else {
                // Process didn't complete within timeout
                process.destroyForcibly();
                System.err.println("Binary execution timed out and was terminated: " + binaryPath);
                System.err.println("Partial logs available at: " + outputLogFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Error executing binary: " + e.getMessage());
            e.printStackTrace();
        }
    }



    /*private void executeBinary(String binaryPath) {
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

            ProcessBuilder processBuilder;
            if (binaryPath.endsWith(".jar")) {
                processBuilder = new ProcessBuilder("java", "-jar", binaryPath);
            } else if (binaryPath.endsWith(".npm")) {
                // For NPM packages, you might want to run npm with different commands
                // This depends on what you're trying to achieve with the NPM package
                processBuilder = new ProcessBuilder("npm", "install", binaryPath);
            } else {
                System.out.println("Unsupported binary type.");
                return;
            }



            // Redirect error stream to standard output
            processBuilder.redirectErrorStream(true);

            // Set working directory if needed
            // processBuilder.directory(new File("/path/to/working/directory"));

            System.out.println("Starting execution of: " + binaryPath);

            // Start the process
            Process process = processBuilder.start();

            // Capture and print the output in real-time
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Binary output: " + line);
                }
            }

            // Wait for the process to complete with timeout
            boolean completed = process.waitFor(5, TimeUnit.MINUTES); // Adjust timeout as needed

            if (completed) {
                int exitCode = process.exitValue();
                if (exitCode == 0) {
                    System.out.println("Binary executed successfully: " + binaryPath);
                } else {
                    System.err.println("Binary execution failed with exit code: " + exitCode);
                }
            } else {
                // Process didn't complete within timeout
                process.destroyForcibly();
                System.err.println("Binary execution timed out and was terminated: " + binaryPath);
            }
        } catch (Exception e) {
            System.err.println("Error executing binary: " + e.getMessage());
            e.printStackTrace();
        }
    }*/




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


    /*private void executeBinary(String binaryPath) {
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



            *//*ProcessBuilder processBuilder;

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
        }*//*
    }*/
}

//asd