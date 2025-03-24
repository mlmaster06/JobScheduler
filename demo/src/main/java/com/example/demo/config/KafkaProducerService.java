package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String jsonMessage) {


        //kafkaTemplate.send(topic, message);
        kafkaTemplate.send(topic, jsonMessage);
        System.out.println("Sent Kafka Message: " + jsonMessage);

    }
}