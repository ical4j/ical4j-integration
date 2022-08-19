package org.ical4j.integration.aws.sns;

import org.ical4j.integration.CalendarListenerSupport;
import org.ical4j.integration.ListenerList;

public class CalendarSnsSubscriber implements CalendarListenerSupport {

    private final ListenerList<Object> listenerList = new ListenerList<>();

    @Override
    public ListenerList<Object> getCalendarListeners() {
        return listenerList;
    }
}
