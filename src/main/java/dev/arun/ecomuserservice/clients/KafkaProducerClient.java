package dev.arun.ecomuserservice.clients;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerClient {
    private KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerClient(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    /*Message: jsonString of whatever data you want to send
     {
       id: 1,
       name: Arun Rao ,
       email: "rao.nayineni@gmail.com"
     }*/
}
