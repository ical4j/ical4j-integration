package org.ical4j.integration;

import net.fortuna.ical4j.model.Calendar;

import java.io.IOException;

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
    Calendar poll(long timeout) throws IOException;
}