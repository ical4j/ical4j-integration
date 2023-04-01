package org.ical4j.integration.aws.sns;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.Message;
import org.ical4j.integration.MessageProducer;

import java.util.function.Supplier;

public class CalendarSnsTransport implements MessageProducer<Calendar> {

    private final AmazonSNS sns;

    private final String topicArn;

    public CalendarSnsTransport(AmazonSNS sns, String topicArn) {
        this.sns = sns;
        this.topicArn = topicArn;
    }

    @Override
    public boolean send(Supplier<Message<Calendar>> calendar) {
        PublishRequest request = new PublishRequest().withMessage("message")
                .withTopicArn(topicArn);
        PublishResult result = sns.publish(request);

        return true;
    }
}
