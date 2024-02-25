package org.ical4j.integration.flow;

import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.CalendarListenerSupport;
import org.ical4j.integration.ListenerList;

import java.util.concurrent.Flow;

/**
 * A {@link java.util.concurrent.Flow.Subscriber} implementation that inspects the METHOD property of
 * calendar data streams and notifies listeners accordingly.
 */
public class CalendarSubscriber implements Flow.Subscriber<Calendar>, CalendarListenerSupport {

    private final ListenerList<Object> listenerList;

    private Flow.Subscription subscription;

    public CalendarSubscriber() {
        this.listenerList = new ListenerList<>();
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        subscription.request(1);
        this.subscription = subscription;
    }

    @Override
    public void onNext(Calendar item) {
        fireCalendarEvent(item);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public ListenerList<Object> getCalendarListeners() {
        return listenerList;
    }
}
