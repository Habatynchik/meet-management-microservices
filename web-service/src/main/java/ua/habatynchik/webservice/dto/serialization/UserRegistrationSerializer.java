package ua.habatynchik.webservice.dto.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ua.habatynchik.webservice.dto.UserRegistrationDto;

import java.util.Map;

@Component
public class UserRegistrationSerializer implements Serializer<UserRegistrationDto> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, UserRegistrationDto data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing UserRegistrationDto: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
    }
}
