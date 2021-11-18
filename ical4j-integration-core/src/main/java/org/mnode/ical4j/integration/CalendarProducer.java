package org.mnode.ical4j.integration;

import net.fortuna.ical4j.model.Calendar;

public interface CalendarProducer {

    void send(Calendar calendar) throws FailedDeliveryException;
}
