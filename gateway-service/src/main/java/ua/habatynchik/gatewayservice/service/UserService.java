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
import ua.habatynchik.gatewayservice.dto.UserDto;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    private final ReplyingKafkaTemplate<String, String, UserDto> replyingKafkaTemplate;

    @Value("${spring.kafka.topic.user-request}")
    private String userRequestTopic;

    @Value("${spring.kafka.consumer.group-id.get-user-group}")
    private String getUserGroup;

    public UserDto getUserByUsername(String username) {
        ProducerRecord<String, String> record = new ProducerRecord<>(userRequestTopic, username);
        record.headers().add(new RecordHeader(KafkaHeaders.GROUP_ID, getUserGroup.getBytes()));

        RequestReplyFuture<String, String, UserDto> future =
                replyingKafkaTemplate.sendAndReceive(record);

        try {
            ConsumerRecord<String, UserDto> consumerRecord = future.get();
            UserDto result = consumerRecord.value();
            log.info("UserService received response: {}", result);
            return result;

        } catch (Exception e) {
            throw new RuntimeException("UserService failed to get response from Kafka", e);
        }
    }

}
