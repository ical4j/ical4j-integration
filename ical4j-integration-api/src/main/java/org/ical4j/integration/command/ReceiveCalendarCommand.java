package org.ical4j.integration.command;

import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.CalendarConsumer;
import picocli.CommandLine;

import java.io.IOException;
import java.util.Optional;

@CommandLine.Command(name = "receive-calendars", description = "Receive calendar objects from consumer stream")
public class ReceiveCalendarCommand implements Runnable {

    private final CalendarConsumer consumer;

    private Optional<Calendar> calendar;

    private long timeout;

    public ReceiveCalendarCommand(CalendarConsumer consumer) {
        this.consumer = consumer;
    }

    public Optional<Calendar> getCalendar() {
        return calendar;
    }

    @Override
    public void run() {
        try {
            this.calendar = consumer.receive(timeout);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
