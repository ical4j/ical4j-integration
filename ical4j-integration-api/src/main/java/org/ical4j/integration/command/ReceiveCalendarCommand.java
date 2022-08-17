package org.ical4j.integration.command;

import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.CalendarConsumer;
import picocli.CommandLine;

@CommandLine.Command(name = "receive-calendars", description = "Receive calendar objects from mail stream")
public class ReceiveCalendarCommand implements Runnable {

    private final CalendarConsumer consumer;

    private Iterable<Calendar> calendars;

    public ReceiveCalendarCommand(CalendarConsumer consumer) {
        this.consumer = consumer;
    }

    public Iterable<Calendar> getCalendars() {
        return calendars;
    }

    @Override
    public void run() {

    }
}
