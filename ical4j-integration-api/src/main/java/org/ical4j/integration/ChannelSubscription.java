package org.ical4j.integration;

import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class ChannelSubscription<T> implements Flow.Subscription {

    private Flow.Subscriber<? super T> subscriber;

    private final AtomicLong requested;

    private final AtomicBoolean cancelled;

    public ChannelSubscription(Flow.Subscriber<? super T> subscriber) {
        this.subscriber = subscriber;
        requested = new AtomicLong();
        cancelled = new AtomicBoolean();
    }

    @Override
    public void request(long n) {
        requested.addAndGet(n);
    }

    public long fulfilled(long n) {
        if (n > requested.get()) {
            throw new IllegalArgumentException("Amount exceeds requested");
        }
        return requested.addAndGet(-n);
    }

    @Override
    public void cancel() {
        cancelled.set(true);
    }

    public boolean isCancelled() {
        return cancelled.get();
    }

    public Flow.Subscriber<? super T> getSubscriber() {
        return subscriber;
    }
}
