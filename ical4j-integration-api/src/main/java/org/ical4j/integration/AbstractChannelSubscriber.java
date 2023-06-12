package org.ical4j.integration;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public abstract class AbstractChannelSubscriber<T> implements ChannelSubscriber<T> {

    private final List<Consumer<T>> subscriberList;

    public AbstractChannelSubscriber() {
        this.subscriberList = new CopyOnWriteArrayList<>();
    }

    protected void publishObject(T t) {
        subscriberList.forEach(s -> s.accept(t));
    }

    @Override
    public void subscribe(Consumer<T> consumer) {
        subscriberList.add(consumer);
    }

    @Override
    public void unsubscribe(Consumer<T> consumer) {
        subscriberList.remove(consumer);
    }
}
