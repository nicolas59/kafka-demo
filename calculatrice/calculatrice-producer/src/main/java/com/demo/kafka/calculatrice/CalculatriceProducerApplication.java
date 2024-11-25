package com.demo.kafka.calculatrice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;

import java.security.SecureRandom;
import java.util.UUID;

@SpringBootApplication
public class CalculatriceProducerApplication implements CommandLineRunner {

    @Autowired
    private KafkaTemplate<String, EventMessage> kafkaTemplate;

    private EventMessage generateEventMessage(){
        var random = new SecureRandom();
        return  new EventMessage(String.valueOf("+-*/".charAt(random.nextInt(4))), random.nextInt(1000), random.nextInt(1000));
    }

    @Override
    public void run(String... args) throws Exception {
        var completableFuture = kafkaTemplate.send("test-demo", UUID.randomUUID().toString(),generateEventMessage());
        completableFuture.thenAccept(item -> {
            System.out.println(item.getProducerRecord().key());
            System.out.println(item.getRecordMetadata());
        });


        kafkaTemplate.setDefaultTopic("test-demo");
        var message = MessageBuilder.withPayload( generateEventMessage())
                .setHeader("event_source", "calculatrice-app")
                .setHeader("correlation-id", UUID.randomUUID().toString())
                .setHeader("kafka_messageKey", UUID.randomUUID().toString())
                .build();
        kafkaTemplate.send(message);
    }

    public static void main(String[] args) {
        SpringApplication.run(CalculatriceProducerApplication.class, args);
    }

}
