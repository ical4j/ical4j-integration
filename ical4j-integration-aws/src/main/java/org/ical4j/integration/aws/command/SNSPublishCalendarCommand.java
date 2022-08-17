package org.ical4j.integration.aws.command;

import picocli.CommandLine;

@CommandLine.Command(name = "publish-calendar", description = "Publish a calendar object to an SNS topic")
public class SNSPublishCalendarCommand implements Runnable {

    @Override
    public void run() {

    }
}
