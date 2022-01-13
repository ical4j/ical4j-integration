package org.ical4j.integration;

import net.fortuna.ical4j.model.Calendar;

public interface CalendarConsumer {

    Calendar poll(long timeout) throws FailedDeliveryException;
}
