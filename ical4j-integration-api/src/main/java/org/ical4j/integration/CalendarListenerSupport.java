package org.ical4j.integration;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import org.ical4j.integration.event.*;

public interface CalendarListenerSupport {

    ListenerList<Object> getCalendarListeners();

    default boolean addCalendarPublishListener(CalendarPublishListener listener) {
        return getCalendarListeners().add(listener);
    }

    default boolean removeCalendarPublishListener(CalendarPublishListener listener) {
        return getCalendarListeners().remove(listener);
    }

    default boolean addCalendarRequestListener(CalendarRequestListener listener) {
        return getCalendarListeners().add(listener);
    }

    default boolean removeCalendarRequestListener(CalendarRequestListener listener) {
        return getCalendarListeners().remove(listener);
    }

    default boolean addCalendarReplyListener(CalendarReplyListener listener) {
        return getCalendarListeners().add(listener);
    }

    default boolean removeCalendarReplyListener(CalendarReplyListener listener) {
        return getCalendarListeners().remove(listener);
    }

    default boolean addCalendarAddListener(CalendarAddListener listener) {
        return getCalendarListeners().add(listener);
    }

    default boolean removeCalendarAddListener(CalendarAddListener listener) {
        return getCalendarListeners().remove(listener);
    }

    default boolean addCalendarCancelListener(CalendarCancelListener listener) {
        return getCalendarListeners().add(listener);
    }

    default boolean removeCalendarCancelListener(CalendarCancelListener listener) {
        return getCalendarListeners().remove(listener);
    }

    default boolean addCalendarRefreshListener(CalendarRefreshListener listener) {
        return getCalendarListeners().add(listener);
    }

    default boolean removeCalendarRefreshListener(CalendarRefreshListener listener) {
        return getCalendarListeners().remove(listener);
    }

    default boolean addCalendarCounterListener(CalendarCounterListener listener) {
        return getCalendarListeners().add(listener);
    }

    default boolean removeCalendarCounterListener(CalendarCounterListener listener) {
        return getCalendarListeners().remove(listener);
    }

    default boolean addCalendarDeclineCounterListener(CalendarDeclineCounterListener listener) {
        return getCalendarListeners().add(listener);
    }

    default boolean removeCalendarDeclineCounterListener(CalendarDeclineCounterListener listener) {
        return getCalendarListeners().remove(listener);
    }

    default void fireCalendarEvent(Calendar calendar) {
        switch (calendar.getProperty(Property.METHOD).getValue()) {
            case "PUBLISH":
                getCalendarListeners().get(CalendarPublishListener.class)
                    .forEach(listener -> listener.onPublish(calendar));
                break;
            case "REQUEST":
                getCalendarListeners().get(CalendarRequestListener.class)
                        .forEach(listener -> listener.onRequest(calendar));
                break;
            case "REPLY":
                getCalendarListeners().get(CalendarReplyListener.class)
                        .forEach(listener -> listener.onReply(calendar));
                break;
            case "ADD":
                getCalendarListeners().get(CalendarAddListener.class)
                        .forEach(listener -> listener.onAdd(calendar));
                break;
            case "CANCEL":
                getCalendarListeners().get(CalendarCancelListener.class)
                        .forEach(listener -> listener.onCancel(calendar));
                break;
            case "REFRESH":
                getCalendarListeners().get(CalendarRefreshListener.class)
                        .forEach(listener -> listener.onRefresh(calendar));
                break;
            case "COUNTER":
                getCalendarListeners().get(CalendarCounterListener.class)
                        .forEach(listener -> listener.onCounter(calendar));
                break;
            case "DECLINE-COUNTER":
                getCalendarListeners().get(CalendarDeclineCounterListener.class)
                        .forEach(listener -> listener.onDeclineCounter(calendar));
                break;
            default:
                break;
        }
    }
}
