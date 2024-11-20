package com.demo.kafka.calculatire;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;

import java.util.UUID;

@SpringBootApplication
public class CalculatireApplication implements CommandLineRunner {

    @Autowired
    private KafkaTemplate<String, EventMessage> kafkaTemplate;

    @Override
    public void run(String... args) throws Exception {
        var completableFuture = kafkaTemplate.send("test-demo", UUID.randomUUID().toString(), new EventMessage("CREATED", "Apple Macbook M4"));
        completableFuture.thenAccept(item -> {
            System.out.println(item.getProducerRecord().key());
            System.out.println(item.getRecordMetadata());
        });


        kafkaTemplate.setDefaultTopic("test-demo");
        var message = MessageBuilder.withPayload(new EventMessage("CREATED", "Apple Macbook M4"))
                .setHeader("event_source", "demo-app")
                .setHeader("kafka_messageKey", UUID.randomUUID().toString())
                .build();
        kafkaTemplate.send(message);
    }

    public static void main(String[] args) {
        SpringApplication.run(CalculatireApplication.class, args);
    }

}
