package org.ical4j.integration;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;

import java.util.List;

public interface CalendarListenerSupport {

    List<CalendarPublishListener> getCalendarPublishListeners();

    default void addCalendarPublishListener(CalendarPublishListener listener) {
        getCalendarPublishListeners().add(listener);
    }

    default void removeCalendarPublishListener(CalendarPublishListener listener) {
        getCalendarPublishListeners().remove(listener);
    }

    default void fireCalendarEvent(Calendar calendar) {
        switch (calendar.getProperty(Property.METHOD).getValue()) {
            case "PUBLISH":
                getCalendarPublishListeners().forEach(listener -> listener.onPublish(calendar));
                break;
            default:
                break;
        }
    }
}
