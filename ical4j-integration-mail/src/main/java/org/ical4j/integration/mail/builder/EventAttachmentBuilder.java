package org.ical4j.integration.mail.builder;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.StyledDescription;
import net.fortuna.ical4j.model.property.Summary;
import org.ical4j.integration.mail.address.Addressing;
import org.ical4j.integration.mail.address.ITipRecipientStrategy;
import org.ical4j.integration.mail.address.RecipientStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Optional;
import java.util.function.Function;

/**
 * Builds a {@link MimeMessage} from a {@link Calendar}, attaching the calendar as a
 * {@code text/calendar} part (with the iTIP {@code method} parameter) plus plain-text and optional
 * HTML body parts. Envelope addressing is derived from the calendar via a {@link RecipientStrategy}
 * by default; any addressing already set by the caller is preserved (caller override).
 */
public class EventAttachmentBuilder implements Function<Calendar, Optional<MimeMessage>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventAttachmentBuilder.class);

    private static final String STYLED_DESCRIPTION = "STYLED-DESCRIPTION";

    private final Session session;

    private final RecipientStrategy recipientStrategy;

    public EventAttachmentBuilder(Session session) {
        this(session, new ITipRecipientStrategy());
    }

    public EventAttachmentBuilder(Session session, RecipientStrategy recipientStrategy) {
        this.session = session;
        this.recipientStrategy = recipientStrategy;
    }

    @Override
    public Optional<MimeMessage> apply(Calendar calendar) {
        try {
            if (calendar.getComponents().isEmpty()) {
                return Optional.empty();
            }

            Optional<Summary> summary = firstProperty(calendar, Property.SUMMARY);
            Optional<Description> description = firstProperty(calendar, Property.DESCRIPTION);
            Optional<StyledDescription> styledDescription = firstProperty(calendar, STYLED_DESCRIPTION);

            StringWriter sout = new StringWriter();
            new CalendarOutputter().output(calendar, sout);

            String method = calendar.<Method>getProperty(Property.METHOD)
                    .map(Property::getValue).orElse(null);

            MimeBodyPart calpart = new MimeBodyPart();
            calpart.setDisposition(MimeBodyPart.ATTACHMENT);
            calpart.setFileName("calendar.ics");
            StringBuilder contentType = new StringBuilder("text/calendar; charset=UTF-8");
            if (method != null) {
                contentType.append("; method=").append(method);
            }
            calpart.setContent(sout.toString(), contentType.toString());

            MimeBodyPart textpart = new MimeBodyPart();
            String text = description.map(Property::getValue)
                    .orElseGet(() -> summary.map(Property::getValue).orElse(""));
            textpart.setContent(text, "text/plain; charset=UTF-8");

            MimeMultipart body = new MimeMultipart();
            body.addBodyPart(calpart);
            body.addBodyPart(textpart);

            if (styledDescription.isPresent()) {
                MimeBodyPart htmlpart = new MimeBodyPart();
                htmlpart.setContent(styledDescription.get().getValue(), "text/html; charset=UTF-8");
                body.addBodyPart(htmlpart);
            }

            MimeMessage message = new MimeMessage(session);
            Optional<Addressing> addressing = recipientStrategy.apply(calendar);
            if (addressing.isPresent()) {
                applyAddressing(message, addressing.get());
            }
            if (summary.isPresent()) {
                message.setSubject(summary.get().getValue());
            }
            message.setContent(body);
            return Optional.of(message);
        } catch (IOException | MessagingException e) {
            LOGGER.error("Unexpected error", e);
        }
        return Optional.empty();
    }

    /**
     * Apply derived addressing only to fields the caller has not already set on the message.
     */
    private void applyAddressing(MimeMessage message, Addressing addressing) throws MessagingException {
        if (addressing.getFrom().isPresent()
                && (message.getFrom() == null || message.getFrom().length == 0)) {
            message.setFrom(addressing.getFrom().get());
        }
        if (!addressing.getTo().isEmpty()
                && message.getRecipients(Message.RecipientType.TO) == null) {
            message.setRecipients(Message.RecipientType.TO,
                    addressing.getTo().toArray(new Address[0]));
        }
        if (!addressing.getCc().isEmpty()
                && message.getRecipients(Message.RecipientType.CC) == null) {
            message.setRecipients(Message.RecipientType.CC,
                    addressing.getCc().toArray(new Address[0]));
        }
    }

    private <T extends Property> Optional<T> firstProperty(Calendar calendar, String name) {
        for (CalendarComponent component : calendar.getComponents()) {
            Optional<T> property = component.getProperty(name);
            if (property.isPresent()) {
                return property;
            }
        }
        return Optional.empty();
    }
}
