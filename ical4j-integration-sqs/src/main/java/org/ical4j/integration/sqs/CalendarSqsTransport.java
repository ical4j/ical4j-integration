package org.ical4j.integration.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.CalendarConsumer;
import org.ical4j.integration.CalendarProducer;
import org.ical4j.integration.FailedDeliveryException;

import java.util.List;

public class CalendarSqsTransport implements CalendarProducer, CalendarConsumer {

    private final AmazonSQS sqs;

    private final String queueUrl;

    public CalendarSqsTransport(AmazonSQS sqs, String queueUrl) {
        this.sqs = sqs;
        this.queueUrl = queueUrl;
    }

    @Override
    public Calendar poll(long timeout) throws FailedDeliveryException {
        List<Message> messages = sqs.receiveMessage(queueUrl).getMessages();
        return null;
    }

    @Override
    public void send(Calendar calendar) {
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody("hello world")
                .withDelaySeconds(5);
        sqs.sendMessage(send_msg_request);
    }
}
