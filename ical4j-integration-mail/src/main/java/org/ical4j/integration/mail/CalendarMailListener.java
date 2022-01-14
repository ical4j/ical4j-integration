package org.ical4j.integration.mail;

import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.CalendarListenerSupport;
import org.ical4j.integration.CalendarPublishListener;

import javax.mail.Message;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import java.util.ArrayList;
import java.util.List;

public class CalendarMailListener implements MessageCountListener, CalendarListenerSupport {

    private List<CalendarPublishListener> publishListenerList = new ArrayList<>();

    private final MessageParser<Calendar> messageParser;

    public CalendarMailListener(MessageParser<Calendar> messageParser) {
        this.messageParser = messageParser;
    }

    @Override
    public void messagesAdded(MessageCountEvent e) {
        for (Message message : e.getMessages()) {
            Calendar calendar = messageParser.parse(message);
            fireCalendarEvent(calendar);
        }
    }

    @Override
    public void messagesRemoved(MessageCountEvent e) {

    }

    @Override
    public List<CalendarPublishListener> getCalendarPublishListeners() {
        return publishListenerList;
    }
}
