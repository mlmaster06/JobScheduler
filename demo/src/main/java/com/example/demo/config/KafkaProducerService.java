package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

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
                System.err.println("Error: messageBody is empty. Skipping Kafka message.");
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
            kafkaTemplate.send(topic, jsonMessage.toString());

            System.out.println("Sent Kafka Message: " + jsonMessage);
        } catch (Exception e) {
            System.err.println("Error sending Kafka message: " + e.getMessage());
        }
    }

}


//asd