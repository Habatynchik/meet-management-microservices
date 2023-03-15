package ua.habatynchik.userservice.dto.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import ua.habatynchik.userservice.dto.UserDto;


import java.util.Map;

public class UserSerializer implements Serializer<UserDto> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, UserDto data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing UserDto: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
    }
}
