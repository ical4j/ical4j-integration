package org.ical4j.integration;

import java.util.function.Consumer;

/**
 * Implementors support retrieval of data objects via polling. Some {@link EgressChannel} implementations
 * will also support polling via implementing this interface.
 */
public interface IngressChannel<T> {

    /**
     * Poll the supported medium for available data objects. The supplied consumer may be
     * called more than once where multiple objects are available.
     * @param consumer a consumer of the available data objects
     * @param timeout the maximum time to wait for available objects
     * @return true if one or more data objects were provided to the consumer
     */
    boolean poll(Consumer<T> consumer, long timeout);

    /**
     * Invoke retrieval of calendar data via supported transport protocol.
     * @param timeout maximum duration of data retrieval
     * @return the retrieved calendar data
     */
    default boolean poll(Consumer<T> consumer, long timeout, boolean autoExpunge) {
        throw new UnsupportedOperationException("This channel doesn't support polling.");
    }

    /**
     * Remove a processed data object from the underlying medium.
     * @param uid an object identifer
     * @return true if the object was successfully removed
     */
    default boolean expunge(String uid) {
        throw new UnsupportedOperationException("This channel doesn't support expunging. "
                + "You must call receive with autoExpunge=true to remove messages from the channel");
    }
}
