package org.mnode.ical4j.integration;

import net.fortuna.ical4j.vcard.VCard;

public interface VCardConsumer {

    VCard poll(long timeout);
}
