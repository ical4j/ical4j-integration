package org.ical4j.integration.aws;

import com.amazonaws.services.sns.model.PublishRequest;

public class SNSRequestBuilder {

    private String topicArn;

    public PublishRequest build() {
        PublishRequest request = new PublishRequest().withMessage("message")
                .withTopicArn(topicArn);
        return request;
    }
}
