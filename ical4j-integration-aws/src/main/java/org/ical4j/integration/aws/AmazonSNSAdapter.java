package org.ical4j.integration.aws;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishResult;
import org.ical4j.integration.ChannelAdapter;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AmazonSNSAdapter<T> implements ChannelAdapter<T> {

    private final AmazonSNS sns;

    private final SNSRequestBuilder requestBuilder;

    public AmazonSNSAdapter(AmazonSNS sns, SNSRequestBuilder requestBuilder) {
        this.sns = sns;
        this.requestBuilder = requestBuilder;
    }

    @Override
    public boolean send(Supplier<T> supplier) {
        PublishResult result = sns.publish(requestBuilder.build());
        return false;
    }

    @Override
    public boolean receive(Consumer<T> consumer, long timeout, boolean autoExpunge) {
        return false;
    }
}
