/*
package com.example.demo.config;

import com.example.demo.jobscheduler.MinioService;
import com.example.demo.jobscheduler.WebSocketController;
import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    public void init() {
        System.out.println("✅ KafkaListeners bean initialized.");
    }

    @KafkaListener(
            topics = "JobSchedulerTopic",
            //groupId = "job-scheduler-group"
            groupId = "#{@environment.getProperty('spring.kafka.consumer.group-id')}"

    )
    public void listenAndExecute(String message) {

        try {
            System.out.println("Raw Kafka message: " + message);
            JSONObject kafkaMessage = new JSONObject(message);
            String messageBody = kafkaMessage.optString("messageBody", "No message body found");
            String binaryPathRaw = kafkaMessage.optString("binaryPath", "");

            System.out.println("Received from Kafka: " + messageBody);
            System.out.println("Raw binary path: " + binaryPathRaw);

            // Try to clean up the binary path if it contains escaped JSON
            String binaryPath = binaryPathRaw;
            if (binaryPathRaw.contains("\\\"")) {
                // Replace escaped quotes
                binaryPath = binaryPathRaw.replace("\\\"", "\"");
                System.out.println("Unescaped binary path: " + binaryPath);
            }

            // Send notification
            webSocketController.sendNotification(messageBody);

            // Download binary
            System.out.println("Attempting to download binary from: " + binaryPath);
            String localPath = minioService.downloadFile(binaryPath);

            if (localPath == null) {
                System.err.println("Failed to download binary. Cannot proceed with execution.");
                return;
            }

            System.out.println("Downloaded binary to local path: " + localPath);

            // Check if the downloaded file exists
            File downloadedFile = new File(localPath);
            System.out.println("File exists: " + downloadedFile.exists());
            System.out.println("File size: " + downloadedFile.length() + " bytes");
            System.out.println("File absolute path: " + downloadedFile.getAbsolutePath());

            // Execute the binary
            executeBinary(localPath);

        } catch (Exception e) {
            System.err.println("Error processing Kafka message: " + e.getMessage());
            e.printStackTrace(); // Add full stack trace
        }
        *//*

*/
/*try {
            System.out.println(message);
            JSONObject kafkaMessage = new JSONObject(message);
            String messageBody = kafkaMessage.optString("messageBody", "No message body found");
            String binaryPath = kafkaMessage.optString("binaryPath", "");

            System.out.println("Received from Kafka: " + messageBody);
            System.out.println("Binary to execute: " + binaryPath);

            //messagingTemplate.convertAndSend("/topic/job-notifications", messageBody);
            webSocketController.sendNotification(messageBody);


            //System.out.println("Message Sent to WebSocket: " + messageBody);
            //String localFilePath = minioService.downloadFile(binaryPath);


            System.out.println("Attempting to download binary from: " + binaryPath);
            String localPath = minioService.downloadFile(binaryPath);
            System.out.println("Downloaded binary to local path: " + localPath);

            // Check if the downloaded file exists and get its details
            File downloadedFile = new File(localPath);
            System.out.println("File exists: " + downloadedFile.exists());
            System.out.println("File size: " + downloadedFile.length() + " bytes");
            System.out.println("File absolute path: " + downloadedFile.getAbsolutePath());

            //String localPath = minioService.downloadFile(binaryPath);

            // Execute the JAR/NPM binary
            executeBinary(localPath);

        } catch (Exception e) {
            System.err.println("Error processing Kafka message: " + e.getMessage());
        }*//*
*/
/*

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
            *//*

*/
/*processBuilder.redirectOutput(ProcessBuilder.Redirect.to(outputLogFile));
            processBuilder.redirectError(ProcessBuilder.Redirect.to(outputLogFile));*//*
*/
/*


            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);


            System.out.println("Starting execution of: " + binaryPath);
            //System.out.println("Logging output to: " + outputLogFile.getAbsolutePath());
            System.out.println("Output will be visible in console: ");

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



    *//*

*/
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
    }*//*
*/
/*





   *//*

*/
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
    }*//*
*/
/*



    *//*

*/
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



            *//*
*/
/*
*//*

*/
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
        }*//*
*/
/*
*//*

*/
/*
    }*//*
*/
/*

}

//asd*//*



package com.example.demo.config;

import com.example.demo.jobscheduler.MinioService;
import com.example.demo.jobscheduler.WebSocketController;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;


@Component
public class KafkaListeners {

    private final SimpMessagingTemplate messagingTemplate;
    private final MinioService minioService;
    private final WebSocketController webSocketController;

    @Autowired
    public KafkaListeners(SimpMessagingTemplate messagingTemplate, MinioService minioService, WebSocketController webSocketController) {
        this.messagingTemplate = messagingTemplate;
        this.minioService = minioService;
        this.webSocketController = webSocketController;
    }

    @PostConstruct
    public void init() {
        System.out.println("✅ KafkaListeners bean initialized.");
    }

    @KafkaListener(
            topics = "JobSchedulerTopic",
            groupId = "#{@environment.getProperty('spring.kafka.consumer.group-id')}"
    )
    public void listenAndExecute(String message) {
        try {
            System.out.println("Received message: " + message);
            JSONObject kafkaMessage = new JSONObject(message);
            String messageBody = kafkaMessage.optString("messageBody", "No message body found");

            // This is the key change - directly get the object name rather than passing
            // the whole JSON or URL to MinioService
            String binaryPath = kafkaMessage.optString("binaryPath", "");

            System.out.println("Received from Kafka: " + messageBody);
            System.out.println("Binary to execute: " + binaryPath);

            webSocketController.sendNotification(messageBody);

            // Pass just the object name/path to minioService
            // Make sure binaryPath is not empty and is a valid object name
            if (binaryPath != null && !binaryPath.isEmpty()) {
                System.out.println("Attempting to download binary from: " + binaryPath);
                String localPath = minioService.downloadFile(binaryPath);

                if (localPath != null && !localPath.isEmpty()) {
                    System.out.println("Downloaded binary to local path: " + localPath);

                    // Check if the downloaded file exists and get its details
                    File downloadedFile = new File(localPath);
                    System.out.println("File exists: " + downloadedFile.exists());
                    System.out.println("File size: " + downloadedFile.length() + " bytes");
                    System.out.println("File absolute path: " + downloadedFile.getAbsolutePath());

                    // Execute the JAR/NPM binary
                    executeBinary(localPath);
                } else {
                    System.err.println("Failed to download binary - localPath is null or empty");
                }
            } else {
                System.err.println("Binary path is null or empty. Cannot download.");
            }

        } catch (Exception e) {
            System.err.println("Error processing Kafka message: " + e.getMessage());
            e.printStackTrace(); // Add this to get more detailed error information
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
            */
/*processBuilder.redirectOutput(ProcessBuilder.Redirect.to(outputLogFile));
            processBuilder.redirectError(ProcessBuilder.Redirect.to(outputLogFile));*//*


            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

            System.out.println("Starting execution of: " + binaryPath);
            System.out.println("Output will be visible in console: ");

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
}*/

package com.example.demo.config;

import com.example.demo.jobscheduler.MinioService;
import com.example.demo.jobscheduler.WebSocketController;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;


@Component
public class KafkaListeners {

    private final SimpMessagingTemplate messagingTemplate;
    private final MinioService minioService;
    private final WebSocketController webSocketController;

    @Autowired
    public KafkaListeners(SimpMessagingTemplate messagingTemplate, MinioService minioService, WebSocketController webSocketController) {
        this.messagingTemplate = messagingTemplate;
        this.minioService = minioService;
        this.webSocketController = webSocketController;
    }

    @PostConstruct
    public void init() {
        System.out.println("✅ KafkaListeners bean initialized.");
    }

    @KafkaListener(
            topics = "JobSchedulerTopic",
            groupId = "#{@environment.getProperty('spring.kafka.consumer.group-id')}"
    )
    public void listenAndExecute(String message) {
        try {
            System.out.println("Received message: " + message);
            JSONObject kafkaMessage = new JSONObject(message);
            String messageBody = kafkaMessage.optString("messageBody", "No message body found");

            // Extract the binaryPath correctly - handle both direct string and nested JSON
            String binaryPath = null;
            if (kafkaMessage.has("binaryPath")) {
                Object binaryPathObj = kafkaMessage.get("binaryPath");

                // Check if it's a nested JSON object
                if (binaryPathObj instanceof JSONObject) {
                    JSONObject binaryPathJson = (JSONObject) binaryPathObj;
                    binaryPath = binaryPathJson.optString("binaryPath", "");
                    System.out.println("Extracted binaryPath from nested JSON object: " + binaryPath);
                }
                // Check if it's a string containing JSON
                else if (binaryPathObj instanceof String) {
                    String binaryPathStr = (String) binaryPathObj;
                    try {
                        JSONObject binaryPathJson = new JSONObject(binaryPathStr);
                        binaryPath = binaryPathJson.optString("binaryPath", "");
                        System.out.println("Extracted binaryPath from JSON string: " + binaryPath);
                    } catch (Exception e) {
                        // Not a JSON string, use as-is
                        binaryPath = binaryPathStr;
                        System.out.println("Using binaryPath as direct string: " + binaryPath);
                    }
                }
            } else if (kafkaMessage.has("metadata")) {
                // Try to extract from metadata if present
                try {
                    JSONObject metadataJson = new JSONObject(kafkaMessage.getString("metadata"));
                    binaryPath = metadataJson.optString("binaryPath", "");
                    System.out.println("Extracted binaryPath from metadata: " + binaryPath);
                } catch (Exception e) {
                    System.err.println("Failed to parse metadata as JSON: " + e.getMessage());
                }
            }

            System.out.println("Received from Kafka: " + messageBody);
            System.out.println("Binary to execute: " + binaryPath);

            webSocketController.sendNotification(messageBody);

            // Only proceed if we have a valid binary path
            if (binaryPath != null && !binaryPath.trim().isEmpty()) {
                System.out.println("Attempting to download binary from: " + binaryPath);
                String localPath = minioService.downloadFile(binaryPath);

                if (localPath != null && !localPath.isEmpty()) {
                    System.out.println("Downloaded binary to local path: " + localPath);

                    // Check if the downloaded file exists and get its details
                    File downloadedFile = new File(localPath);
                    System.out.println("File exists: " + downloadedFile.exists());
                    System.out.println("File size: " + downloadedFile.length() + " bytes");
                    System.out.println("File absolute path: " + downloadedFile.getAbsolutePath());

                    // Execute the JAR/NPM binary
                    executeBinary(localPath);
                } else {
                    System.err.println("Failed to download binary - localPath is null or empty");
                }
            } else {
                System.err.println("Binary path is null or empty. Cannot download.");
            }

        } catch (Exception e) {
            System.err.println("Error processing Kafka message: " + e.getMessage());
            e.printStackTrace(); // Add this to get more detailed error information
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

            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

            System.out.println("Starting execution of: " + binaryPath);
            System.out.println("Output will be visible in console: ");

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
}