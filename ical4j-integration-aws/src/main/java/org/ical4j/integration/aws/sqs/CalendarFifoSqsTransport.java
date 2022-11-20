package org.ical4j.integration.aws.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.CalendarConsumer;
import org.ical4j.integration.CalendarProducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CalendarFifoSqsTransport implements CalendarProducer, CalendarConsumer {

    private final AmazonSQS sqs;

    private final String queueUrl;

    private String messageGroupId;

    public CalendarFifoSqsTransport(AmazonSQS sqs, String queueUrl) {
        this(sqs, queueUrl, null);
    }

    public CalendarFifoSqsTransport(AmazonSQS sqs, String queueUrl, String messageGroupId) {
        this.sqs = sqs;
        this.queueUrl = queueUrl;
        this.messageGroupId = messageGroupId;
    }

    @Override
    public Optional<Calendar> receive(long timeout) throws IOException {
        List<Message> messages = new ArrayList<>();
        try {
            messages.addAll(sqs.receiveMessage(queueUrl).getMessages());
            //TODO: parse and return calendar
            return Optional.empty();
        } finally {
            // purge messages if all calendars parse correctly..
            messages.forEach(message -> sqs.deleteMessage(queueUrl, message.getReceiptHandle()));
        }
    }

    @Override
    public void send(Calendar calendar) {
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl).withMessageGroupId(messageGroupId)
                .withMessageDeduplicationId(getDeduplicationId(calendar))
                .withMessageBody(calendar.toString())
                .withDelaySeconds(5);
        sqs.sendMessage(send_msg_request);
    }

    private String getDeduplicationId(Calendar calendar) {
        return String.valueOf(calendar.hashCode());
    }
}
