package ua.habatynchik.webservice.service;

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
import ua.habatynchik.webservice.dto.UserRegistrationDto;

@Service
@Log4j2
@RequiredArgsConstructor
public class RegistrationService {

    private final ReplyingKafkaTemplate<String, UserRegistrationDto, String> replyingKafkaTemplate;

    @Value("${spring.kafka.topic.reg-request}")
    private String regRequestTopic;

    public String registerUser(UserRegistrationDto userRegistrationDto) {
        ProducerRecord<String, UserRegistrationDto> record = new ProducerRecord<>(regRequestTopic, userRegistrationDto);
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, regRequestTopic.getBytes()));

        RequestReplyFuture<String, UserRegistrationDto, String> future =
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
