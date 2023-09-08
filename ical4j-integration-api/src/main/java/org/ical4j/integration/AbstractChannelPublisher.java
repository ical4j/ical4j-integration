package org.ical4j.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;

public abstract class AbstractChannelPublisher<T> implements ChannelPublisher<T> {

    private final List<ChannelSubscription<T>> subscriptionList;

    public AbstractChannelPublisher() {
        subscriptionList = new ArrayList<>();
    }

    @Override
    public ChannelSubscription<T> register(Flow.Subscriber<? super T> subscriber) {
        ChannelSubscription<T> subscription = new ChannelSubscription<>(subscriber);
        subscriptionList.add(subscription);
        return subscription;
    }

    @Override
    public List<ChannelSubscription<T>> listSubscriptions() {
        return subscriptionList;
    }

    protected void publish(T object) {
        subscriptionList.forEach(s -> {
            if (s.isCancelled()) {
                s.getSubscriber().onComplete();
            } else {
                s.getSubscriber().onNext(object);
            }
        });
    }
}
