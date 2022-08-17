package org.ical4j.integration.aws.command;

import picocli.CommandLine;

@CommandLine.Command(name = "send-calendar", description = "Send a calendar object to an SQS queue")
public class SQSSendCalendarCommand implements Runnable {

    @Override
    public void run() {

    }
}
