package org.ical4j.integration.mail;

import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.CalendarListenerSupport;
import org.ical4j.integration.ListenerList;

import javax.mail.Message;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import java.util.Optional;

public class CalendarMailListener implements MessageCountListener, CalendarListenerSupport {

    private final ListenerList<Object> listenerList = new ListenerList<>();

    private final MessageParser<Calendar> messageParser;

    public CalendarMailListener(MessageParser<Calendar> messageParser) {
        this.messageParser = messageParser;
    }

    @Override
    public void messagesAdded(MessageCountEvent e) {
        for (Message message : e.getMessages()) {
            Optional<Calendar> calendar = messageParser.parse(message);
            calendar.ifPresent(this::fireCalendarEvent);
        }
    }

    @Override
    public void messagesRemoved(MessageCountEvent e) {

    }

    @Override
    public ListenerList<Object> getCalendarListeners() {
        return listenerList;
    }
}
