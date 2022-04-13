package org.ical4j.integration.jms;

import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.CalendarListenerSupport;
import org.ical4j.integration.ListenerList;

import javax.jms.Message;
import javax.jms.MessageListener;

public class CalendarJmsListener implements MessageListener, CalendarListenerSupport {

    private final ListenerList<Object> listenerList = new ListenerList<>();

    @Override
    public void onMessage(Message message) {
        //TODO: parse and forward calendar object
        Calendar calendar = null;
        fireCalendarEvent(calendar);
    }

    @Override
    public ListenerList<Object> getCalendarListeners() {
        return listenerList;
    }
}
