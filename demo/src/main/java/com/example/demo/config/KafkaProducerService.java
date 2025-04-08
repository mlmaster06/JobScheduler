package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /*public void sendMessage(String topic, String jsonMessage) {


        //kafkaTemplate.send(topic, message);
        kafkaTemplate.send(topic, jsonMessage);
        System.out.println("Sent Kafka Message: " + jsonMessage);

    }*/

    public void sendMessage(String topic, String messageBody, String binaryPath) {
        try {
            if (messageBody == null || messageBody.isEmpty()) {
                logger.warn("Error: messageBody is empty. Skipping Kafka message.");
                return;
            }
            if (binaryPath == null) {
                binaryPath = ""; // Default to empty string
            }

            // Create a JSON object to send structured data
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("messageBody", messageBody);
            jsonMessage.put("binaryPath", binaryPath);

            // Send JSON message to Kafka
            //kafkaTemplate.send(topic, jsonMessage.toString());
            /*kafkaTemplate.send(topic, jsonMessage.toString())
                    .addCallback(
                            result -> System.out.println("Message sent successfully: " + result.getRecordMetadata()),
                            ex -> System.out.println("Failed to send message: " + ex.getMessage())
                    );*/

            kafkaTemplate.send(topic, jsonMessage.toString())
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            logger.info("✅ Kafka message sent successfully!");
                            logger.debug("➡ Topic: " + result.getRecordMetadata().topic());
                            logger.debug("➡ Partition: " + result.getRecordMetadata().partition());
                            logger.debug("➡ Offset: " + result.getRecordMetadata().offset());
                        } else {
                            logger.error("❌ Failed to send Kafka message: " ,ex);
                        }
                    });


            //System.out.println("Sent Kafka Message: " + jsonMessage);
        } catch (Exception e) {
            logger.error("Error sending Kafka message: " + e);
        }
    }

}


//asd