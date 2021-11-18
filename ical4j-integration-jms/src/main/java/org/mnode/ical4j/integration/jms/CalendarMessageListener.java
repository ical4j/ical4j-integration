package org.mnode.ical4j.integration.jms;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import org.mnode.ical4j.integration.CalendarPublishListener;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.ArrayList;
import java.util.List;

public class CalendarMessageListener implements MessageListener {

    private List<CalendarPublishListener> publishListenerList = new ArrayList<>();

    @Override
    public void onMessage(Message message) {
        //TODO: parse and forward calendar object
        Calendar calendar = null;

        switch (calendar.getProperty(Property.METHOD).getValue()) {
            case "PUBLISH":
                publishListenerList.forEach(listener -> listener.onPublish(calendar));
            default:
                break;
        }
    }

    public void addCalendarPublishListener(CalendarPublishListener listener) {
        publishListenerList.add(listener);
    }

    public void removeCalendarPublishListener(CalendarPublishListener listener) {
        publishListenerList.remove(listener);
    }
}
