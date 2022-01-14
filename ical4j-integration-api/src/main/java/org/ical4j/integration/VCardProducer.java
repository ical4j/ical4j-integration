package org.ical4j.integration;

import net.fortuna.ical4j.vcard.VCard;

/**
 * Implementors of this interface support transmission of vCard data via some transport
 * protocol. For example, SMTP, HTTP, etc.
 */
public interface VCardProducer {

    /**
     * Invoke transfer of card data via supported transport protocol.
     * @param card card data to transfer
     * @throws FailedDeliveryException when transfer fails
     */
    void send(VCard card) throws FailedDeliveryException;
}
