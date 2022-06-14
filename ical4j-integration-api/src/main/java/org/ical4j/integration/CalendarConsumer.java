package org.ical4j.integration;

import net.fortuna.ical4j.model.Calendar;

import java.io.IOException;
import java.util.Optional;

/**
 * Implementors of this interface support retrieval of iCalendar data via some transport
 * protocol. For example, IMAP, POP3, HTTP, etc.
 */
public interface CalendarConsumer {

    /**
     * Invoke retrieval of calendar data via supported transport protocol.
     * @param timeout maximum duration of data retrieval
     * @return the retrieved calendar data
     * @throws IOException if data retrieval fails
     */
    Optional<Calendar> receive(long timeout) throws IOException;
}
