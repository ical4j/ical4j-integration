package org.ical4j.integration.sns;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.CalendarProducer;

public class CalendarSnsTransport implements CalendarProducer {

    private final AmazonSNS sns;

    private final String topicArn;

    public CalendarSnsTransport(AmazonSNS sns, String topicArn) {
        this.sns = sns;
        this.topicArn = topicArn;
    }

    @Override
    public void send(Calendar calendar) {
        PublishRequest request = new PublishRequest()
                .withMessage("message")
                .withTopicArn(topicArn);
        PublishResult result = sns.publish(request);
    }
}
