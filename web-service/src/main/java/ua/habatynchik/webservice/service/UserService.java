package ua.habatynchik.webservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import ua.habatynchik.webservice.dto.UserRegistrationDto;

import java.util.concurrent.ExecutionException;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserService {

    private final ReplyingKafkaTemplate<String, UserRegistrationDto, String> replyingKafkaTemplate;

    @Value("${spring.kafka.topic.auth-request}")
    private String authRequestTopic;

    public void registerUser(UserRegistrationDto userRegistrationDto) {
        ProducerRecord<String, UserRegistrationDto> record = new ProducerRecord<>(authRequestTopic, userRegistrationDto);
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, authRequestTopic.getBytes()));
        RequestReplyFuture<String, UserRegistrationDto, String> future =
                replyingKafkaTemplate.sendAndReceive(record);
        try {
            ConsumerRecord<String, String> consumerRecord = future.get();
            String result = consumerRecord.value();
            log.info("Received response: {}", result);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get response from Kafka", e);
        }
    }

}
