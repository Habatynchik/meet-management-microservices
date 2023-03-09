package ua.habatynchik.webservice.service;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import ua.habatynchik.webservice.dto.UserLoginDto;

@Service
@Log4j2
public class LoginService {

    private final ReplyingKafkaTemplate<String, UserLoginDto, String> replyingKafkaTemplate;

    @Value("${spring.kafka.topic.auth-request}")
    private String authRequestTopic;

    public LoginService(ReplyingKafkaTemplate<String, UserLoginDto, String> replyingKafkaTemplate) {
        this.replyingKafkaTemplate = replyingKafkaTemplate;
    }

    public String loginUser(UserLoginDto userLoginDto) {
        ProducerRecord<String, UserLoginDto> record = new ProducerRecord<>(authRequestTopic, userLoginDto);
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, authRequestTopic.getBytes()));

        RequestReplyFuture<String, UserLoginDto, String> future =
                replyingKafkaTemplate.sendAndReceive(record);

        try {
            ConsumerRecord<String, String> consumerRecord = future.get();
            String result = consumerRecord.value();
            log.info("LoginService received response: {}", result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("LoginService failed to get response from Kafka", e);
        }
    }
}
