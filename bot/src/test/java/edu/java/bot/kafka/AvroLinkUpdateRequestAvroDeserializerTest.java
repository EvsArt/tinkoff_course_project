package edu.java.bot.kafka;

import edu.java.bot.dto.api.LinkUpdateRequest;
import java.util.List;
import edu.java.bot.service.LinkUpdateRequestAvroDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AvroLinkUpdateRequestAvroDeserializerTest {

    private static final AvroLinkUpdateRequestSerializerForTests serializer =
        new AvroLinkUpdateRequestSerializerForTests();
    private static final LinkUpdateRequestAvroDeserializer deserializer = new LinkUpdateRequestAvroDeserializer();
    private final LinkUpdateRequest request = LinkUpdateRequest.builder()
        .url("aaa")
        .description("vvv")
        .tgChatIds(List.of(1L, 2L, 3L))
        .build();
    private final byte[] serializedRequest = serializer.serialize("myTop", request);

    @AfterAll
    public static void closeAll() {
        serializer.close();
        deserializer.close();
    }

    @Test
    void testLinkUpdateRequestDeserializingForTests() {
        LinkUpdateRequest res = deserializer.deserialize("myTop", serializedRequest);

        assertThat(res).isEqualTo(request);
    }

}
