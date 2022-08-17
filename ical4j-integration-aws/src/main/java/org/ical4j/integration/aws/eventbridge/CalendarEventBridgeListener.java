package org.ical4j.integration.aws.eventbridge;

import org.ical4j.integration.CalendarListenerSupport;
import org.ical4j.integration.ListenerList;

public class CalendarEventBridgeListener implements CalendarListenerSupport {

    private final ListenerList<Object> listenerList = new ListenerList<>();

    @Override
    public ListenerList<Object> getCalendarListeners() {
        return listenerList;
    }
}
