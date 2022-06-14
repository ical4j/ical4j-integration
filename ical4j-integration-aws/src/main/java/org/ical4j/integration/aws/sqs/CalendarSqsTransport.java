package org.ical4j.integration.aws.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.CalendarConsumer;
import org.ical4j.integration.CalendarProducer;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class CalendarSqsTransport implements CalendarProducer, CalendarConsumer {

    private final AmazonSQS sqs;

    private final String queueUrl;

    public CalendarSqsTransport(AmazonSQS sqs, String queueUrl) {
        this.sqs = sqs;
        this.queueUrl = queueUrl;
    }

    @Override
    public Optional<Calendar> receive(long timeout) throws IOException {
        List<Message> messages = sqs.receiveMessage(queueUrl).getMessages();
        //TODO: parse and return calendar
        return Optional.empty();
    }

    @Override
    public void send(Calendar calendar) {
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl).withMessageBody("hello world")
                .withDelaySeconds(5);
        sqs.sendMessage(send_msg_request);
    }
}
