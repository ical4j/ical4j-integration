package org.ical4j.integration.command;

import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.Message;
import picocli.CommandLine;

@CommandLine.Command(name = "send-calendar", description = "Send a calendar object to producer recipients")
public class SendCalendarCommand extends AbstractEndpointCommand<Calendar, Boolean> {

    private Calendar calendar;

    public SendCalendarCommand withCalendar(Calendar calendar) {
        this.calendar = calendar;
        return this;
    }

    @Override
    public void run() {
        getConsumer().accept(getEndpoint().send(() -> new Message<>(null, calendar)));
    }
}
