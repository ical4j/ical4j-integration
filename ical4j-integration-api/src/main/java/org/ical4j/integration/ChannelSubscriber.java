package org.ical4j.integration;

import java.util.function.Consumer;

public interface ChannelSubscriber<T> {

    void subscribe(Consumer<T> consumer);

    void unsubscribe(Consumer<T> consumer);
}
