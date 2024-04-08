package edu.java.bot.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.RecordDeserializationException;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class KafkaErrorHandler implements CommonErrorHandler {

    private final KafkaDlqSender dlqSender;

    KafkaErrorHandler(KafkaDlqSender dlqSender) {
        this.dlqSender = dlqSender;
    }

    @Override
    public boolean handleOne(
        Exception exception,
        ConsumerRecord<?, ?> record,
        Consumer<?, ?> consumer,
        MessageListenerContainer container
    ) {
        log.error("Handling exception with available record");
        String msg = "record: " + record.toString() + '\n' +
                "exception: " + exception.getMessage();
        dlqSender.send(msg);
        handle(exception, consumer);
        return true;
    }

    @Override
    public void handleOtherException(
        Exception exception,
        Consumer<?, ?> consumer,
        MessageListenerContainer container,
        boolean batchListener
    ) {
        log.error("Handling exception without available record");
        dlqSender.send("exception: " + exception.getMessage());
        handle(exception, consumer);
    }

    private void handle(Exception exception, Consumer<?, ?> consumer) {
        log.error("Exception thrown", exception);
        if (exception instanceof RecordDeserializationException ex) {
            consumer.seek(ex.topicPartition(), ex.offset() + 1L);
            consumer.commitSync();
        } else {
            log.error("Exception not handled", exception);
        }
    }
}
