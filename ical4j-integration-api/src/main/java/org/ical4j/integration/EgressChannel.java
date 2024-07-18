package org.ical4j.integration;

import java.util.function.Supplier;

/**
 * Implementors support content adaptation for transport over a supported channel.
 * @param <T> the payload type
 */
public interface EgressChannel<T> {

    /**
     * Invoke transfer of data via supported transport protocol.
     * @param supplier source of data to transfer
     */
    boolean send(Supplier<T> supplier);
}
