package edu.java.kafka;

import edu.java.dto.bot.LinkUpdateRequest;
import edu.java.configuration.kafka.LinkUpdateRequestSerializer;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AvroLinkUpdateRequestSerializerTest {

    private static final LinkUpdateRequestSerializer serializer = new LinkUpdateRequestSerializer();
    private static final AvroDeserializerForTests deserializer = new AvroDeserializerForTests();

    private final LinkUpdateRequest request = new LinkUpdateRequest(List.of(1L, 2L, 3L), "aaa", "vvv");
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
