package org.ical4j.integration.aws.command;

import picocli.CommandLine;

@CommandLine.Command(name = "send-card", description = "Send a vCard object to an SQS queue")
public class SQSSendCardCommand implements Runnable {

    @Override
    public void run() {

    }
}
