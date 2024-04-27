package edu.java.bot.service;

import edu.java.bot.avro.AvroLinkUpdateRequest;
import edu.java.bot.dto.api.LinkUpdateRequest;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.kafka.support.serializer.DeserializationException;

@Slf4j
public class LinkUpdateRequestAvroDeserializer implements Deserializer<LinkUpdateRequest> {

    @Override
    public LinkUpdateRequest deserialize(String topic, byte[] data) {
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(AvroLinkUpdateRequest.getClassSchema());
        SeekableByteArrayInput arrayInput = new SeekableByteArrayInput(data);

        DataFileReader<GenericRecord> dataFileReader;
        GenericRecord readedRecord = null;
        try {
            dataFileReader = new DataFileReader<>(arrayInput, datumReader);
            readedRecord = dataFileReader.next();
            return toLinkUpdateRequest(readedRecord);
        } catch (IOException e) {
            log.error("Error with serialization data: {} in topic {}", data, topic);
            throw new DeserializationException("Error with deserialization data", data, false, e);
        }
    }

    private LinkUpdateRequest toLinkUpdateRequest(GenericRecord avro) {
        return LinkUpdateRequest.builder()
            .tgChatIds((List<Long>) avro.get("tgChatIds"))
            .url(String.valueOf(avro.get("url")))
            .description(String.valueOf(avro.get("description")))
            .build();
    }
}
