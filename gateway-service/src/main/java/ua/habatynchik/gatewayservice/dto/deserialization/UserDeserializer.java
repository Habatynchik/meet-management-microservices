package ua.habatynchik.gatewayservice.dto.deserialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import ua.habatynchik.gatewayservice.dto.UserDto;

import java.util.Map;

public class UserDeserializer implements Deserializer<UserDto> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public UserDto deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, UserDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing UserLoginDto: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
    }
}