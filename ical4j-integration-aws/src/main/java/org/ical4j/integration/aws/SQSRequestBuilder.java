package org.ical4j.integration.aws;

import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SQSRequestBuilder<T> {

    private String queueUrl;

    private String deduplicationId;

    private String messageGroupId;

    private T body;

    public SendMessageRequest build() {
        return new SendMessageRequest()
                .withQueueUrl(queueUrl).withMessageGroupId(messageGroupId)
                .withMessageDeduplicationId(deduplicationId)
                .withMessageBody(body.toString())
                .withDelaySeconds(5);
    }
}
