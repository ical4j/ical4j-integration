package org.ical4j.integration;

import net.fortuna.ical4j.vcard.VCard;

import java.io.IOException;

public interface VCardConsumer {

    /**
     * Invoke retrieval of calendar data via supported transport protocol.
     * @param timeout maximum duration of data retrieval
     * @return the retrieved calendar data
     * @throws IOException if data retrieval fails
     */
    VCard poll(long timeout) throws IOException;
}
