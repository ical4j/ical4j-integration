package org.ical4j.integration.mail.address;

import jakarta.mail.internet.InternetAddress;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Immutable value type describing the email envelope addressing derived from a calendar object:
 * an optional {@code From} address and (possibly empty) {@code To} and {@code Cc} lists.
 */
public final class Addressing {

    private final InternetAddress from;

    private final List<InternetAddress> to;

    private final List<InternetAddress> cc;

    public Addressing(InternetAddress from, List<InternetAddress> to, List<InternetAddress> cc) {
        this.from = from;
        this.to = to != null ? List.copyOf(to) : Collections.emptyList();
        this.cc = cc != null ? List.copyOf(cc) : Collections.emptyList();
    }

    /**
     * @return the derived sender address, if one could be determined
     */
    public Optional<InternetAddress> getFrom() {
        return Optional.ofNullable(from);
    }

    /**
     * @return the derived primary recipients (never null, possibly empty)
     */
    public List<InternetAddress> getTo() {
        return to;
    }

    /**
     * @return the derived carbon-copy recipients (never null, possibly empty)
     */
    public List<InternetAddress> getCc() {
        return cc;
    }

    /**
     * @return true if no sender and no recipients could be derived
     */
    public boolean isEmpty() {
        return from == null && to.isEmpty() && cc.isEmpty();
    }
}
