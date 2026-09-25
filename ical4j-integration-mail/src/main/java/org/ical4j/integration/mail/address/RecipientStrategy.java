package org.ical4j.integration.mail.address;

import net.fortuna.ical4j.model.Calendar;

import java.util.Optional;
import java.util.function.Function;

/**
 * Derives email envelope {@link Addressing} (from/to/cc) from an iCalendar object.
 *
 * <p>Implementations return {@link Optional#empty()} when addressing cannot be derived, leaving
 * the caller responsible for setting addressing explicitly (caller override).</p>
 */
public interface RecipientStrategy extends Function<Calendar, Optional<Addressing>> {
}
