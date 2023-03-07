package ua.habatynchik.authenticationservice.dto.deserialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import ua.habatynchik.authenticationservice.dto.UserLoginDto;

import java.util.Map;

public class UserLoginDeserializer implements Deserializer<UserLoginDto> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public UserLoginDto deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, UserLoginDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing UserLoginDto: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
    }
}
