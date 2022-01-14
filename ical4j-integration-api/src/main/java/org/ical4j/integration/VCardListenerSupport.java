package org.ical4j.integration;

import net.fortuna.ical4j.vcard.VCard;

public interface VCardListenerSupport {

    ListenerList<VCardListener> getVCardListeners();

    default void addVCardListener(VCardListener listener) {
        getVCardListeners().add(listener);
    }

    default void removeVCardListener(VCardListener listener) {
        getVCardListeners().remove(listener);
    }

    default void fireVCardEvent(VCard card) {
        getVCardListeners().getAll().forEach(listener -> listener.onUpdate(card));
    }
}
