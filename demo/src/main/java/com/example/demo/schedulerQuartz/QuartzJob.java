package com.example.demo.schedulerQuartz;

import com.example.demo.config.KafkaProducerService;
import com.example.demo.jobscheduler.MinioService;
import com.example.demo.jobscheduler.ScheduledMessage;
import com.example.demo.jobscheduler.ScheduledMessageService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class QuartzJob implements Job {

    @Autowired
    private ScheduledMessageService messageService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private MinioService minioService;


    //private KafkaTemplate<String, String> kafkaTemplate;


    /*public void execute(JobExecutionContext context) {
        LocalDateTime now = LocalDateTime.now();

        List<ScheduledMessage> messages = messageService.getPendingMessagesBefore(now);

        for (ScheduledMessage msg : messages) {
            try {

                kafkaTemplate.send(msg.getKafkaTopic(), msg.getMessageBody());

                // Mark message as SENT
                messageService.updateMessageStatus(msg.getId(), "SENT");

                System.out.println("Message sent to Kafka: " + msg.getMessageBody());
            } catch (Exception e) {
                // Mark message as FAILED
                messageService.updateMessageStatus(msg.getId(), "FAILED");
                System.err.println("Error sending message: " + e.getMessage());
            }
        }
    }*/


    /*public void execute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        *//*messageService = (ScheduledMessageService) jobDataMap.get("messageService");
        kafkaTemplate = (KafkaTemplate<String, String>) jobDataMap.get("kafkaTemplate");*//*

        ScheduledMessageService messageService = (ScheduledMessageService) jobDataMap.get("messageService");
        KafkaTemplate<String, String> kafkaTemplate = (KafkaTemplate<String, String>) jobDataMap.get("kafkaTemplate");

        if (messageService == null || kafkaTemplate == null) {
            System.err.println("Error: messageService or kafkaTemplate is null!");
            return;
        }

        List<ScheduledMessage> messages = messageService.getPendingMessagesBefore(LocalDateTime.now());

        for (ScheduledMessage msg : messages) {
            try {
                kafkaTemplate.send(msg.getKafkaTopic(), msg.getMessageBody());
                messageService.updateMessageStatus(msg.getId(), "SENT");
                System.out.println("Message sent to Kafka: " + msg.getMessageBody());
            } catch (Exception e) {
                messageService.updateMessageStatus(msg.getId(), "FAILED");
                System.err.println("Error sending message: " + e.getMessage());
            }
        }
    }*/

    @Override
    public void execute(JobExecutionContext context) {
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


    private String extractBinaryPath(String metadataJson) {
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

}
