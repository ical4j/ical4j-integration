package org.ical4j.integration;

import java.io.IOException;
import java.util.concurrent.Flow;

/**
 * Implementors support consumption of channel data via polling. Some {@link EgressChannel} implementations
 * will also support polling via implementing this interface.
 */
public interface IngressChannel<T> extends Flow.Publisher<T> {

    default boolean poll(long timeout) {
        return poll(timeout, false);
    }

    /**
     * Invoke retrieval of calendar data via supported transport protocol.
     * @param timeout maximum duration of data retrieval
     * @return the retrieved calendar data
     * @throws IOException if data retrieval fails
     */
    default boolean poll(long timeout, boolean autoExpunge) {
        throw new UnsupportedOperationException("This channel doesn't support polling.");
    }

    /**
     * Remove a processed calendar from the underlying queue.
     * @param uid
     * @return
     */
    default boolean expunge(String uid) {
        throw new UnsupportedOperationException("This channel doesn't support expunging. "
                + "You must call receive with autoExpunge=true to remove messages from the queue");
    }
}
