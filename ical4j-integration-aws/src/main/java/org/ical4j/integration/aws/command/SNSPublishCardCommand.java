package org.ical4j.integration.aws.command;

import picocli.CommandLine;

@CommandLine.Command(name = "publish-card", description = "Publish a vCard object to an SNS topic")
public class SNSPublishCardCommand implements Runnable {

    @Override
    public void run() {

    }
}
