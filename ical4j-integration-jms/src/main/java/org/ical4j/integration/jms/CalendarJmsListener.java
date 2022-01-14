package org.ical4j.integration.jms;

import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.CalendarListenerSupport;
import org.ical4j.integration.CalendarPublishListener;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.ArrayList;
import java.util.List;

public class CalendarJmsListener implements MessageListener, CalendarListenerSupport {

    private List<CalendarPublishListener> publishListenerList = new ArrayList<>();

    @Override
    public void onMessage(Message message) {
        //TODO: parse and forward calendar object
        Calendar calendar = null;
        fireCalendarEvent(calendar);
    }

    @Override
    public List<CalendarPublishListener> getCalendarPublishListeners() {
        return publishListenerList;
    }
}
