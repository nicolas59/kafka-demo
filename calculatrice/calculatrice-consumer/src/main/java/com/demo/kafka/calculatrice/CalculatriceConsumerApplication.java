package com.demo.kafka.calculatrice;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SimpleKafkaHeaderMapper;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
public class CalculatriceConsumerApplication implements CommandLineRunner {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.topic}")
    private String topic;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;


    @Autowired
    private KafkaTemplate<String, EventMessage> kafkaTemplate;

    @Override
    public void run(String... args) throws Exception {
        KafkaConsumer<String, EventMessage> consumer = new KafkaConsumer<>(consumerConfigs());
        consumer.subscribe(List.of(topic));

        while(true){
            var record = consumer.poll(Duration.ofSeconds(10));
            if(!record.isEmpty()){
                record.forEach(item ->{
                    var eventMessage = item.value();
                    var result = switch (eventMessage.operator()){
                        case "+" -> eventMessage.left()+eventMessage.right();
                        case "-" -> eventMessage.left()-eventMessage.right();
                        case "*" -> eventMessage.left()*eventMessage.right();
                        case "/" -> eventMessage.left()/eventMessage.right();
                        default -> null;
                    };

                    System.out.println("-------------------------------");
                    System.out.println("Headers :");
                    item.headers().forEach(header ->{
                        System.out.println("%s : %s".formatted(header.key(), new String(header.value())));
                    });
                    System.out.println("Key : %s,  %d %s %d = %d".formatted(item.key(), eventMessage.left(), eventMessage.operator(), eventMessage.right(), result));
                });
            }else{
                System.out.println("Aucun message");
            }
        }
    }

    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class);
        //props.put(ConsumerConfig.GROUP_ID_CONFIG, "helloworld");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return props;
    }

    public static void main(String[] args) {
        SpringApplication.run(CalculatriceConsumerApplication.class, args);
    }
}
