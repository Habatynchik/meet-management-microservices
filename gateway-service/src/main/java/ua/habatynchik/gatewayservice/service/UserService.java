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

    @Value("${spring.kafka.consumer.group-id.get-user-by-id-group}")
    private String getUserByIdGroup;

    @Value("${spring.kafka.consumer.group-id.get-user-by-username-group}")
    private String getUserByUsernameGroup;

    public UserDto getUserByUsername(String username) {
        return getUser(username, getUserByUsernameGroup);
    }

    public UserDto getUserById(Long id) {
        return getUser(String.valueOf(id), getUserByIdGroup);
    }

    private UserDto getUser(String key, String groupId) {
        ProducerRecord<String, String> record = new ProducerRecord<>(userRequestTopic, key);
        record.headers().add(new RecordHeader(KafkaHeaders.GROUP_ID, groupId.getBytes()));

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

    public boolean hasRole(Long userId, String role){
        UserDto user = getUserById(userId);

        return user.getRole().name().equals(role);
    }

}
