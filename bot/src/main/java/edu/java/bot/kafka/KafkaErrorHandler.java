package edu.java.bot.kafka;

import edu.java.bot.metrics.ErrorUpdate;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.RecordDeserializationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(prefix = "app", name = "useQueue", havingValue = "true")
class KafkaErrorHandler implements CommonErrorHandler {

    private final KafkaDlqSender dlqSender;

    KafkaErrorHandler(KafkaDlqSender dlqSender) {
        this.dlqSender = dlqSender;
    }

    @Override
    public boolean handleOne(
        Exception exception,
        ConsumerRecord<?, ?> readedRecord,
        Consumer<?, ?> consumer,
        MessageListenerContainer container
    ) {
        log.error("Handling exception with available record");
        String msg = "record: " + readedRecord.toString() + '\n'
            + "exception: " + exception.getMessage();
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
        dlqSender.send("with exception: " + exception.getMessage());
        handle(exception, consumer);
    }

    @ErrorUpdate
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
