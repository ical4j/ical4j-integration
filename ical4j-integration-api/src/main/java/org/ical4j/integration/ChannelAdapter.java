package org.ical4j.integration;

import java.util.function.Supplier;

/**
 * Implementors support content adaptation for transport over a supported channel.
 */
public interface ChannelAdapter<T> {

    /**
     * Invoke transfer of data via supported transport protocol.
     * @param supplier source of data to transfer
     */
    boolean publish(Supplier<T> supplier);
}
