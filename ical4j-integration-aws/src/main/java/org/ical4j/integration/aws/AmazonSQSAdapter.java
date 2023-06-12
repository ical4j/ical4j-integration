package org.ical4j.integration.aws;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageResult;
import com.amazonaws.services.sqs.model.Message;
import org.ical4j.integration.ChannelAdapter;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AmazonSQSAdapter<T> implements ChannelAdapter<T> {

    private final AmazonSQS sqs;

    private final String queueUrl;

    private final SQSRequestBuilder<T> requestBuilder;

    public AmazonSQSAdapter(AmazonSQS sqs, String queueUrl, SQSRequestBuilder<T> requestBuilder) {
        this.sqs = sqs;
        this.queueUrl = queueUrl;
        this.requestBuilder = requestBuilder;
    }

    @Override
    public boolean send(Supplier<T> supplier) {
        sqs.sendMessage(requestBuilder.build());
        return false;
    }

    @Override
    public boolean receive(Consumer<T> consumer, long timeout, boolean autoExpunge) {
        return false;
    }

    public boolean expunge(String uid) {
        Message message = null;
        DeleteMessageResult result = sqs.deleteMessage(queueUrl, message.getReceiptHandle());
        return result.getSdkHttpMetadata().getHttpStatusCode() < 300;
    }
}
