package edu.java.configuration.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.botClient.dto.LinkUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;

@Slf4j
public class LinkUpdateRequestSerializer implements Serializer<LinkUpdateRequest> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, LinkUpdateRequest data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            log.error("Error with serialize message: {}", data);
            throw new RuntimeException(e);
        }
    }
}
