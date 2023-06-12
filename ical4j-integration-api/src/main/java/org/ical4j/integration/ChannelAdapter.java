package org.ical4j.integration;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Implementors support content adaptation for transport over a supported channel.
 */
public interface ChannelAdapter<T> {

    /**
     * Invoke transfer of data via supported transport protocol.
     * @param supplier source of data to transfer
     */
    boolean send(Supplier<T> supplier);

    default boolean receive(Consumer<T> consumer, long timeout) {
        return receive(consumer, timeout, false);
    }

    /**
     * Invoke retrieval of calendar data via supported transport protocol.
     * @param timeout maximum duration of data retrieval
     * @return the retrieved calendar data
     * @throws IOException if data retrieval fails
     */
    boolean receive(Consumer<T> consumer, long timeout, boolean autoExpunge);

    /**
     * Remove a processed calendar from the underlying queue.
     * @param uid
     * @return
     */
    default boolean expunge(String uid) {
        throw new UnsupportedOperationException("This implementation doesn't support expunging. "
                + "You must call receive with autoExpunge=true to remove messages from the queue");
    }
}
