package org.ical4j.integration;

import net.fortuna.ical4j.vcard.VCard;
import org.ical4j.integration.event.VCardListener;

/**
 * Provide support for notifying card listeners of events.
 */
public interface VCardListenerSupport {

    ListenerList<VCardListener> getVCardListeners();

    /**
     * Register listener for card events.
     * @param listener a vcard listener
     * @return true if listener was registered successfully
     */
    default void addVCardListener(VCardListener listener) {
        getVCardListeners().add(listener);
    }

    /**
     * Unregister listener for card events.
     * @param listener a vcard listener
     * @return true if listener was unregistered successfully
     */
    default void removeVCardListener(VCardListener listener) {
        getVCardListeners().remove(listener);
    }

    default void fireVCardEvent(VCard card) {
        getVCardListeners().getAll().forEach(listener -> listener.onUpdate(card));
    }
}
