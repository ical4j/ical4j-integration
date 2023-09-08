package org.ical4j.integration;

import java.util.List;
import java.util.concurrent.Flow;

/**
 * Implementors observe a data stream from a channel and publish this data to registered subscribers.
 */
public interface ChannelPublisher<T> extends Flow.Publisher<T>, AutoCloseable {

    /**
     * Register a new subscriber.
     * @param subscriber
     * @return the subscription associated with a successful registration
     */
    ChannelSubscription<T> register(Flow.Subscriber<? super T> subscriber);

    List<ChannelSubscription<T>> listSubscriptions();

    @Override
    default void subscribe(Flow.Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(register(subscriber));
    }

    @Override
    default void close() {
        listSubscriptions().forEach(s -> s.getSubscriber().onComplete());
    }
}
