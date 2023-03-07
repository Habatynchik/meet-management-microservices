package ua.habatynchik.webservice.dto.deserialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import ua.habatynchik.webservice.dto.UserRegistrationDto;

import java.util.Map;

public class UserRegistrationDeserializer implements Deserializer<UserRegistrationDto> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public UserRegistrationDto deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, UserRegistrationDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing UserRegistrationDto: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
    }
}
