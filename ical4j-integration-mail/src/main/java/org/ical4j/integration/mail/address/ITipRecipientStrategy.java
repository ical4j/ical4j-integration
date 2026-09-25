package org.ical4j.integration.mail.address;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.model.parameter.SentBy;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Derives envelope addressing from an iCalendar object following the iTIP (RFC 5546) {@code METHOD}
 * matrix:
 *
 * <pre>
 *   REQUEST / CANCEL / ADD     organizer -&gt; attendees
 *   REPLY / COUNTER / REFRESH  attendee  -&gt; organizer
 *   DECLINECOUNTER             organizer -&gt; attendee
 *   PUBLISH (or no method)     organizer -&gt; (caller-supplied recipients)
 * </pre>
 *
 * <p>A {@code CAL-ADDRESS} value is mapped to an email address by stripping the {@code mailto:}
 * scheme; the {@code CN} parameter supplies the personal name. The organizer {@code SENT-BY}
 * parameter, when present, overrides the {@code From}. Attendees with {@code CUTYPE} of
 * {@code RESOURCE} or {@code ROOM} are excluded. When the properties required for the method are
 * absent (or an attendee-originated method has no single identifiable attendee), no addressing is
 * derived and {@link Optional#empty()} is returned so the caller can supply addressing.</p>
 */
public class ITipRecipientStrategy implements RecipientStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ITipRecipientStrategy.class);

    @Override
    public Optional<Addressing> apply(Calendar calendar) {
        String method = calendar.<Method>getProperty(Property.METHOD)
                .map(Property::getValue).orElse(Method.VALUE_PUBLISH);

        Optional<InternetAddress> organizer = firstOrganizer(calendar).flatMap(this::organizerAddress);
        List<InternetAddress> attendees = attendeeAddresses(calendar);

        Addressing addressing;
        if (Method.VALUE_REQUEST.equals(method) || Method.VALUE_CANCEL.equals(method)
                || Method.VALUE_ADD.equals(method)) {
            // organizer -> attendees
            if (organizer.isEmpty() || attendees.isEmpty()) {
                return Optional.empty();
            }
            addressing = new Addressing(organizer.get(), attendees, null);
        } else if (Method.VALUE_REPLY.equals(method) || Method.VALUE_COUNTER.equals(method)
                || Method.VALUE_REFRESH.equals(method)) {
            // attendee -> organizer (only when a single attendee can be identified)
            if (organizer.isEmpty() || attendees.size() != 1) {
                return Optional.empty();
            }
            addressing = new Addressing(attendees.get(0), List.of(organizer.get()), null);
        } else if (Method.VALUE_DECLINECOUNTER.equals(method)) {
            // organizer -> the attendee
            if (organizer.isEmpty() || attendees.size() != 1) {
                return Optional.empty();
            }
            addressing = new Addressing(organizer.get(), List.of(attendees.get(0)), null);
        } else {
            // PUBLISH (or unknown/no method): sender only, recipients supplied by the caller
            if (organizer.isEmpty()) {
                return Optional.empty();
            }
            addressing = new Addressing(organizer.get(), Collections.emptyList(), null);
        }
        return addressing.isEmpty() ? Optional.empty() : Optional.of(addressing);
    }

    private Optional<Organizer> firstOrganizer(Calendar calendar) {
        for (CalendarComponent component : calendar.getComponents()) {
            Optional<Organizer> organizer = component.getProperty(Property.ORGANIZER);
            if (organizer.isPresent()) {
                return organizer;
            }
        }
        return Optional.empty();
    }

    private List<InternetAddress> attendeeAddresses(Calendar calendar) {
        // union of attendees across all components, de-duplicated by cal-address
        Map<String, Attendee> byAddress = new LinkedHashMap<>();
        for (CalendarComponent component : calendar.getComponents()) {
            List<Attendee> attendees = component.getProperties(Property.ATTENDEE);
            for (Attendee attendee : attendees) {
                URI calAddress = attendee.getCalAddress();
                String key = calAddress != null ? calAddress.toString() : attendee.getValue();
                byAddress.putIfAbsent(key, attendee);
            }
        }
        List<InternetAddress> result = new ArrayList<>();
        for (Attendee attendee : byAddress.values()) {
            attendeeAddress(attendee).ifPresent(result::add);
        }
        return result;
    }

    private Optional<InternetAddress> attendeeAddress(Attendee attendee) {
        Optional<CuType> cuType = attendee.getParameter(Parameter.CUTYPE);
        if (cuType.isPresent()) {
            String value = cuType.get().getValue();
            if (CuType.RESOURCE.getValue().equalsIgnoreCase(value)
                    || CuType.ROOM.getValue().equalsIgnoreCase(value)) {
                return Optional.empty();
            }
        }
        return toAddress(attendee.getCalAddress(), attendee.getParameter(Parameter.CN));
    }

    private Optional<InternetAddress> organizerAddress(Organizer organizer) {
        Optional<Cn> cn = organizer.getParameter(Parameter.CN);
        Optional<SentBy> sentBy = organizer.getParameter(Parameter.SENT_BY);
        if (sentBy.isPresent()) {
            return toAddress(sentBy.get().getAddress(), cn);
        }
        return toAddress(organizer.getCalAddress(), cn);
    }

    private Optional<InternetAddress> toAddress(URI calAddress, Optional<Cn> cn) {
        if (calAddress == null) {
            return Optional.empty();
        }
        String scheme = calAddress.getScheme();
        if (scheme == null || !"mailto".equalsIgnoreCase(scheme)) {
            return Optional.empty();
        }
        String email = calAddress.getSchemeSpecificPart();
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        try {
            InternetAddress address = new InternetAddress(email.trim());
            if (cn.isPresent()) {
                address.setPersonal(cn.get().getValue());
            }
            return Optional.of(address);
        } catch (AddressException | UnsupportedEncodingException e) {
            LOGGER.warn("Skipping invalid calendar address: {}", calAddress, e);
            return Optional.empty();
        }
    }

    /**
     * Local constants for parameter names to avoid magic strings.
     */
    private static final class Parameter {
        static final String CN = "CN";
        static final String CUTYPE = "CUTYPE";
        static final String SENT_BY = "SENT-BY";
    }
}
