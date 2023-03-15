package ua.habatynchik.userservice.dto.deserialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import ua.habatynchik.userservice.model.User;

import java.util.Map;

public class UserDeserializer implements Deserializer<User> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public User deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, User.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing UserLoginDto: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
    }
}