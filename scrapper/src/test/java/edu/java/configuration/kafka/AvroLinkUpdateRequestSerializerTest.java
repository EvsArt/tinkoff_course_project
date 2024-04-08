package edu.java.configuration.kafka;

import edu.java.avro.AvroLinkUpdateRequest;
import edu.java.botClient.dto.LinkUpdateRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.junit.jupiter.api.Test;

class AvroLinkUpdateRequestSerializerTest {

    @Test
    void serialize() {

        LinkUpdateRequest request = new LinkUpdateRequest(List.of(1L, 2L, 3L), "aaa", "vvv");

        try (var s = new LinkUpdateRequestSerializer()) {
            byte[] res = s.serialize("aa", request);
            System.out.println(Arrays.toString(res));
            System.out.println(deserialize("aa", res).getDescription());
        }

    }

    public LinkUpdateRequest deserialize(String s, byte[] data) {

        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(AvroLinkUpdateRequest.getClassSchema());
        SeekableByteArrayInput arrayInput = new SeekableByteArrayInput(data);

        DataFileReader<GenericRecord> dataFileReader = null;
        GenericRecord record = null;
        try {
            dataFileReader = new DataFileReader<>(arrayInput, datumReader);
            record = dataFileReader.next();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return toLinkUpdateRequest(record);
    }

    private LinkUpdateRequest toLinkUpdateRequest(GenericRecord avro) {
        return LinkUpdateRequest.builder()
            .tgChatIds((List<Long>) avro.get("tgChatIds"))
            .url(String.valueOf(avro.get("url")))
            .description(String.valueOf(avro.get("description")))
            .build();
    }

}
