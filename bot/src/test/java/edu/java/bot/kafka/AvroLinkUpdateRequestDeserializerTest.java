package edu.java.bot.kafka;

import edu.java.bot.api.dto.LinkUpdateRequest;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AvroLinkUpdateRequestDeserializerTest {

    LinkUpdateRequest request = LinkUpdateRequest.builder()
        .url("aaa")
        .description("vvv")
        .tgChatIds(List.of(1L, 2L, 3L))
        .build();

    byte[] serializedRequest =
        new byte[] {79, 98, 106, 1, 2, 22, 97, 118, 114, 111, 46, 115, 99, 104, 101, 109, 97, -96, 3, 123, 34, 116, 121,
            112, 101, 34, 58, 34, 114, 101, 99, 111, 114, 100, 34, 44, 34, 110, 97, 109, 101, 34, 58, 34, 76, 105, 110,
            107, 85, 112, 100, 97, 116, 101, 82, 101, 113, 117, 101, 115, 116, 34, 44, 34, 110, 97, 109, 101, 115, 112,
            97, 99, 101, 34, 58, 34, 101, 100, 117, 46, 106, 97, 118, 97, 34, 44, 34, 102, 105, 101, 108, 100, 115, 34,
            58, 91, 123, 34, 110, 97, 109, 101, 34, 58, 34, 116, 103, 67, 104, 97, 116, 73, 100, 115, 34, 44, 34, 116,
            121, 112, 101, 34, 58, 123, 34, 116, 121, 112, 101, 34, 58, 34, 97, 114, 114, 97, 121, 34, 44, 34, 105, 116,
            101, 109, 115, 34, 58, 34, 108, 111, 110, 103, 34, 125, 125, 44, 123, 34, 110, 97, 109, 101, 34, 58, 34,
            117, 114, 108, 34, 44, 34, 116, 121, 112, 101, 34, 58, 34, 115, 116, 114, 105, 110, 103, 34, 125, 44, 123,
            34, 110, 97, 109, 101, 34, 58, 34, 100, 101, 115, 99, 114, 105, 112, 116, 105, 111, 110, 34, 44, 34, 116,
            121, 112, 101, 34, 58, 34, 115, 116, 114, 105, 110, 103, 34, 125, 93, 125, 0, 40, -16, -30, -105, 75, -75,
            -111, 41, 41, 63, 44, -56, -121, 70, 63, -58, 2, 26, 6, 2, 4, 6, 0, 6, 97, 97, 97, 6, 118, 118, 118, 40,
            -16, -30, -105, 75, -75, -111, 41, 41, 63, 44, -56, -121, 70, 63, -58};

    @Test
    void testLinkUpdateRequestDeserializer() {
        var deserializer = new LinkUpdateRequestDeserializer();

        LinkUpdateRequest res = deserializer.deserialize("myTop", serializedRequest);
        deserializer.close();

        assertThat(res).isEqualTo(request);
    }

    @Test
    void testLinkUpdateRequestSerializingForTests() {
        var serializer = new AvroLinkUpdateRequestSerializerForTests();
        var deserializer = new LinkUpdateRequestDeserializer();
        byte[] serialized = serializer.serialize("myTop", request);
        serializer.close();

        LinkUpdateRequest res = deserializer.deserialize("myTop", serialized);
        deserializer.close();

        assertThat(res).isEqualTo(request);
    }

}
