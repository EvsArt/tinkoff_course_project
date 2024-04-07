package edu.java.bot.configuration.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.api.dto.LinkUpdateRequest;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

@Slf4j
public class LinkUpdateRequestDeserializer implements Deserializer<LinkUpdateRequest> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public LinkUpdateRequest deserialize(String s, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, LinkUpdateRequest.class);
        } catch (IOException e) {
            log.error("Error with deserialization data {}", bytes);
            throw new RuntimeException(e);
        }
    }
}
