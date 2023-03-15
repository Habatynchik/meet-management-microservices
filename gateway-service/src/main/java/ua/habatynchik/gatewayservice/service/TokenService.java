package ua.habatynchik.gatewayservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class TokenService {

    private final ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate;

    @Value("${spring.kafka.topic.jwt-request}")
    private String jwtRequestTopic;

    @Value("${spring.kafka.consumer.group-id.group-refresh}")
    private String jwtRefreshGroup;

    @Value("${spring.kafka.consumer.group-id.group-jwt}")
    private String jwtValidateGroup;

    public String refreshToken(String token) {

        ProducerRecord<String, String> record = new ProducerRecord<>(jwtRequestTopic, token);
        record.headers().add(new RecordHeader(KafkaHeaders.GROUP_ID, jwtRefreshGroup.getBytes()));

        RequestReplyFuture<String, String, String> future =
                replyingKafkaTemplate.sendAndReceive(record);

        try {
            ConsumerRecord<String, String> consumerRecord = future.get();

            String result = consumerRecord.value();
            log.info("Received response: {}", result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get response from Kafka", e);
        }

    }

    public String validateToken(String token) {

        ProducerRecord<String, String> record = new ProducerRecord<>(jwtRequestTopic, token);
        record.headers().add(new RecordHeader(KafkaHeaders.GROUP_ID, jwtValidateGroup.getBytes()));

        RequestReplyFuture<String, String, String> future =
                replyingKafkaTemplate.sendAndReceive(record);

        try {
            ConsumerRecord<String, String> consumerRecord = future.get();

            String result = consumerRecord.value();
            log.info("Received response: {}", result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get response from Kafka", e);
        }

    }
}
