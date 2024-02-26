package org.ical4j.integration;

import java.util.concurrent.Flow;
import java.util.function.Supplier;

/**
 * Implementors support content adaptation for transport over a supported channel.
 * @param <T> the payload type
 * @param <P> the published message type following successful sending of a payload
 */
public interface EgressChannel<T, P> extends Flow.Publisher<P> {

    /**
     * Invoke transfer of data via supported transport protocol.
     * @param supplier source of data to transfer
     */
    boolean send(Supplier<T> supplier);
}
