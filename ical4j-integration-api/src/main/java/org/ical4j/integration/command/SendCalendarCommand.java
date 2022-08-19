package org.ical4j.integration.command;

import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.CalendarProducer;
import org.ical4j.integration.FailedDeliveryException;
import picocli.CommandLine;

@CommandLine.Command(name = "send-calendar", description = "Send a calendar object to producer recipients")
public class SendCalendarCommand implements Runnable{

    private final CalendarProducer producer;

    private Calendar calendar;

    public SendCalendarCommand(CalendarProducer producer) {
        this.producer = producer;
    }

    public SendCalendarCommand withCalendar(Calendar calendar) {
        this.calendar = calendar;
        return this;
    }

    @Override
    public void run() {
        try {
            producer.send(calendar);
        } catch (FailedDeliveryException e) {
            throw new RuntimeException(e);
        }
    }
}
