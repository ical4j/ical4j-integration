package org.ical4j.integration.aws.command;

import picocli.CommandLine;

@CommandLine.Command(name = "receive-card", description = "Retrieve a vCard object from an SQS queue")
public class SQSReceiveCardCommand implements Runnable {

    @Override
    public void run() {

    }
}
