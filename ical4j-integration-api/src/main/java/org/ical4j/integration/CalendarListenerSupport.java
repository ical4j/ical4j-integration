package org.ical4j.integration;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import org.ical4j.integration.event.*;


/**
 * Provide support for notifying calendar listeners of events.
 */
public interface CalendarListenerSupport {

    ListenerList<Object> getCalendarListeners();

    /**
     * Register listener for calendar {@link net.fortuna.ical4j.model.property.immutable.ImmutableMethod.PUBLISH} events.
     * @param listener a calendar publish listener
     * @return true if listener was registered successfully
     */
    default boolean addCalendarPublishListener(CalendarPublishListener listener) {
        return getCalendarListeners().add(listener);
    }

    /**
     * Unregister listener for calendar {@link net.fortuna.ical4j.model.property.immutable.ImmutableMethod.PUBLISH} events.
     * @param listener a calendar publish listener
     * @return true if listener was unregistered successfully
     */
    default boolean removeCalendarPublishListener(CalendarPublishListener listener) {
        return getCalendarListeners().remove(listener);
    }

    /**
     * Register listener for calendar {@link net.fortuna.ical4j.model.property.immutable.ImmutableMethod.REQUEST} events.
     * @param listener a calendar request listener
     * @return true if listener was registered successfully
     */
    default boolean addCalendarRequestListener(CalendarRequestListener listener) {
        return getCalendarListeners().add(listener);
    }

    /**
     * Unregister listener for calendar {@link net.fortuna.ical4j.model.property.immutable.ImmutableMethod.REQUEST} events.
     * @param listener a calendar request listener
     * @return true if listener was unregistered successfully
     */
    default boolean removeCalendarRequestListener(CalendarRequestListener listener) {
        return getCalendarListeners().remove(listener);
    }

    /**
     * Register listener for calendar {@link net.fortuna.ical4j.model.property.immutable.ImmutableMethod.REPLY} events.
     * @param listener a calendar reply listener
     * @return true if listener was registered successfully
     */
    default boolean addCalendarReplyListener(CalendarReplyListener listener) {
        return getCalendarListeners().add(listener);
    }

    /**
     * Unregister listener for calendar {@link net.fortuna.ical4j.model.property.immutable.ImmutableMethod.REPLY} events.
     * @param listener a calendar reply listener
     * @return true if listener was unregistered successfully
     */
    default boolean removeCalendarReplyListener(CalendarReplyListener listener) {
        return getCalendarListeners().remove(listener);
    }

    /**
     * Register listener for calendar {@link net.fortuna.ical4j.model.property.immutable.ImmutableMethod.ADD} events.
     * @param listener a calendar add listener
     * @return true if listener was registered successfully
     */
    default boolean addCalendarAddListener(CalendarAddListener listener) {
        return getCalendarListeners().add(listener);
    }

    /**
     * Unregister listener for calendar {@link net.fortuna.ical4j.model.property.immutable.ImmutableMethod.ADD} events.
     * @param listener a calendar add listener
     * @return true if listener was unregistered successfully
     */
    default boolean removeCalendarAddListener(CalendarAddListener listener) {
        return getCalendarListeners().remove(listener);
    }

    /**
     * Register listener for calendar {@link net.fortuna.ical4j.model.property.immutable.ImmutableMethod.CANCEL} events.
     * @param listener a calendar cancel listener
     * @return true if listener was registered successfully
     */
    default boolean addCalendarCancelListener(CalendarCancelListener listener) {
        return getCalendarListeners().add(listener);
    }

    /**
     * Unregister listener for calendar {@link net.fortuna.ical4j.model.property.immutable.ImmutableMethod.CANCEL} events.
     * @param listener a calendar cancel listener
     * @return true if listener was unregistered successfully
     */
    default boolean removeCalendarCancelListener(CalendarCancelListener listener) {
        return getCalendarListeners().remove(listener);
    }

    /**
     * Register listener for calendar {@link net.fortuna.ical4j.model.property.immutable.ImmutableMethod.REFRESH} events.
     * @param listener a calendar refresh listener
     * @return true if listener was registered successfully
     */
    default boolean addCalendarRefreshListener(CalendarRefreshListener listener) {
        return getCalendarListeners().add(listener);
    }

    /**
     * Unregister listener for calendar {@link net.fortuna.ical4j.model.property.immutable.ImmutableMethod.REFRESH} events.
     * @param listener a calendar refresh listener
     * @return true if listener was unregistered successfully
     */
    default boolean removeCalendarRefreshListener(CalendarRefreshListener listener) {
        return getCalendarListeners().remove(listener);
    }

    /**
     * Register listener for calendar {@link net.fortuna.ical4j.model.property.immutable.ImmutableMethod.COUNTER} events.
     * @param listener a calendar counter listener
     * @return true if listener was registered successfully
     */
    default boolean addCalendarCounterListener(CalendarCounterListener listener) {
        return getCalendarListeners().add(listener);
    }

    /**
     * Unregister listener for calendar {@link net.fortuna.ical4j.model.property.immutable.ImmutableMethod.COUNTER} events.
     * @param listener a calendar counter listener
     * @return true if listener was unregistered successfully
     */
    default boolean removeCalendarCounterListener(CalendarCounterListener listener) {
        return getCalendarListeners().remove(listener);
    }

    /**
     * Register listener for calendar {@link net.fortuna.ical4j.model.property.immutable.ImmutableMethod.DECLINE_COUNTER} events.
     * @param listener a calendar decline counter listener
     * @return true if listener was registered successfully
     */
    default boolean addCalendarDeclineCounterListener(CalendarDeclineCounterListener listener) {
        return getCalendarListeners().add(listener);
    }

    /**
     * Unregister listener for calendar {@link net.fortuna.ical4j.model.property.immutable.ImmutableMethod.DECLINE_COUNTER} events.
     * @param listener a calendar decline counter listener
     * @return true if listener was unregistered successfully
     */
    default boolean removeCalendarDeclineCounterListener(CalendarDeclineCounterListener listener) {
        return getCalendarListeners().remove(listener);
    }

    default void fireCalendarEvent(Calendar calendar) {
        switch (calendar.getRequiredProperty(Property.METHOD).getValue()) {
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
