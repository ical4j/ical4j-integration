package org.ical4j.integration;

import java.util.function.Supplier;

/**
 * Implementors provide a transport mechanism for supported data objects. Typical
 * implementations would support transfer of iCalendar or vCard objects.
 * @param <T> the object type
 */
public interface EgressChannel<T> {

    /**
     * Invoke transfer of data via the supported transport protocol.
     * @param supplier source of data to transfer
     */
    boolean send(Supplier<T> supplier);
}
