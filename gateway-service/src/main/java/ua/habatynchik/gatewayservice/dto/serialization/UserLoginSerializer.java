package ua.habatynchik.gatewayservice.dto.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import ua.habatynchik.gatewayservice.dto.UserLoginDto;

import java.util.Map;

public class UserLoginSerializer implements Serializer<UserLoginDto> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, UserLoginDto data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing UserLoginDto: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
    }
}
