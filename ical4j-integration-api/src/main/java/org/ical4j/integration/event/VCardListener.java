package org.ical4j.integration.event;

import net.fortuna.ical4j.vcard.VCard;

/**
 * Event listener for consumption of vCard object updates.
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6350.html#section-7.2.4">vCard - Simultaneous Editing</a>
 */
public interface VCardListener {

    void onUpdate(VCard card);
}
