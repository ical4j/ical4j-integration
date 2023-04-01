package org.ical4j.integration.command;

import net.fortuna.ical4j.model.Calendar;
import picocli.CommandLine;

import java.util.function.Consumer;

@CommandLine.Command(name = "receive-calendar", description = "Receive calendar objects from consumer stream")
public class ReceiveCalendarCommand extends AbstractConsumerCommand<Calendar, Calendar> {

    private long timeout;

    private boolean autoExpunge;

    public ReceiveCalendarCommand(Consumer<Calendar> consumer) {
        super(consumer);
    }

    @Override
    public void run() {
        getEndpoint().receive(getConsumer(), timeout, autoExpunge);
    }
}
