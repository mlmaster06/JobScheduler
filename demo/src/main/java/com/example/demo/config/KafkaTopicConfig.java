package com.example.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    //public static final String TOPIC_DELAYED_MESSAGE = "delayed_message";

    @Bean
    public NewTopic jobSchedulerTopic() {
        //return new NewTopic("demo_topic", 1, (short) 1);
        return TopicBuilder.name("JobSchedulerTopic")   // yaha name vahi dalenge jis naam se topic Terminal me create kia hai
                .build();
    }
}
