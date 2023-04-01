package org.ical4j.integration;

import java.util.function.Supplier;

/**
 * Implementors of this interface support transmission of iCalendar data via some transport
 * protocol. For example, SMTP, HTTP, etc.
 */
public interface MessageProducer<T> {

    /**
     * Invoke transfer of data via supported transport protocol.
     * @param supplier source of data to transfer
     */
    boolean send(Supplier<Message<T>> supplier);
}
