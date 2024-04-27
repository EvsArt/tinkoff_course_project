package edu.java.service.kafka;

import edu.java.avro.AvroLinkUpdateRequest;
import edu.java.dto.bot.LinkUpdateRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serializer;

@Slf4j
public class LinkUpdateRequestSerializer implements Serializer<LinkUpdateRequest> {

    @Override
    public byte[] serialize(String topic, LinkUpdateRequest request) {
        byte[] retVal = null;
        AvroLinkUpdateRequest avro = toAvro(request);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GenericDatumWriter<GenericRecord> datumWriter =
            new GenericDatumWriter<>(AvroLinkUpdateRequest.getClassSchema());

        try (DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            dataFileWriter.create(AvroLinkUpdateRequest.getClassSchema(), outputStream);

            dataFileWriter.append(avro);

            dataFileWriter.flush();

            retVal = outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error with serializing data: {}", request);
        }
        return retVal;
    }

    private AvroLinkUpdateRequest toAvro(LinkUpdateRequest request) {
        return AvroLinkUpdateRequest.newBuilder()
            .setTgChatIds(request.getTgChatIds())
            .setUrl(request.getUrl())
            .setDescription(request.getDescription())
            .build();
    }

}
