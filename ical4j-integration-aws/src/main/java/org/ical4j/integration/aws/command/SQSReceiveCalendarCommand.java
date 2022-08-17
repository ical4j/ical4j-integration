package org.ical4j.integration.aws.command;

import picocli.CommandLine;

@CommandLine.Command(name = "receive-calendar", description = "Retrieve a calendar object from an SQS queue")
public class SQSReceiveCalendarCommand implements Runnable {

    @Override
    public void run() {

    }
}
