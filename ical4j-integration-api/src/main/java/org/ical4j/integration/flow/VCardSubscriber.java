package org.ical4j.integration.flow;

import net.fortuna.ical4j.vcard.VCard;
import org.ical4j.integration.ListenerList;
import org.ical4j.integration.VCardListenerSupport;
import org.ical4j.integration.event.VCardListener;

import java.util.concurrent.Flow;

/**
 * A {@link Flow.Subscriber} implementation that notifies listeners.
 */
public class VCardSubscriber implements Flow.Subscriber<VCard>, VCardListenerSupport {

    private final ListenerList<VCardListener> listenerList;

    private Flow.Subscription subscription;

    public VCardSubscriber() {
        this.listenerList = new ListenerList<>();
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        subscription.request(1);
        this.subscription = subscription;
    }

    @Override
    public void onNext(VCard item) {
        fireVCardEvent(item);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public ListenerList<VCardListener> getVCardListeners() {
        return listenerList;
    }
}
