package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableKafka
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class,
		org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class})
@EnableScheduling
public class JobSchedulerApplication {

	public static void main(String[] args) {

		SpringApplication.run(JobSchedulerApplication.class, args);
	}

	// //This is using Dependency Injection as RESTful API endpoints are not defined yet
	/*@Bean
	CommandLineRunner commandLineRunner(KafkaTemplate<String, String> kafkaTemplate) {
		return args -> {
			//kafkaTemplate.send("JobSchedulerTopic","Hello Kafka ;-)");  //Topic is defined in KafkaTopicConfig
			for (int i=0; i<100; i++) {
				kafkaTemplate.send("JobSchedulerTopic","Hello Kafka ;-)" + i);

			}

		};
	}*/
}
//asd