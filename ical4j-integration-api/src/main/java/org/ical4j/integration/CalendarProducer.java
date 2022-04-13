package org.ical4j.integration;

import net.fortuna.ical4j.model.Calendar;

/**
 * Implementors of this interface support transmission of iCalendar data via some transport
 * protocol. For example, SMTP, HTTP, etc.
 */
public interface CalendarProducer {

    /**
     * Invoke transfer of calendar data via supported transport protocol.
     * @param calendar calendar data to transfer
     * @throws FailedDeliveryException when transfer fails
     */
    void send(Calendar calendar) throws FailedDeliveryException;
}
