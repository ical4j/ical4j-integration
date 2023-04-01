package org.ical4j.integration.aws.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.MessageConsumer;
import org.ical4j.integration.MessageProducer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CalendarFifoSqsTransport implements MessageProducer<Calendar>, MessageConsumer<Calendar> {

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
    public boolean receive(Consumer<Calendar> consumer, long timeout, boolean autoExpunge) {
        List<Message> messages = new ArrayList<>();
        try {
            messages.addAll(sqs.receiveMessage(queueUrl).getMessages());
            //TODO: parse and return calendar
            return false;
        } finally {
            if (autoExpunge) {
                // purge messages if all calendars parse correctly..
                messages.forEach(message -> sqs.deleteMessage(queueUrl, message.getReceiptHandle()));
            }
        }
    }

    @Override
    public boolean expunge(String uid) {
        Message message = null;
        DeleteMessageResult result = sqs.deleteMessage(queueUrl, message.getReceiptHandle());
        return result.getSdkHttpMetadata().getHttpStatusCode() < 300;
    }

    @Override
    public boolean send(Supplier<org.ical4j.integration.Message<Calendar>> calendar) {
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl).withMessageGroupId(messageGroupId)
                .withMessageDeduplicationId(getDeduplicationId(calendar.get().getBody()))
                .withMessageBody(calendar.toString())
                .withDelaySeconds(5);
        sqs.sendMessage(send_msg_request);

        return true;
    }

    private String getDeduplicationId(Calendar calendar) {
        return String.valueOf(calendar.hashCode());
    }
}
