package org.ical4j.integration.mail.processor;

import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeMessage;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;

/**
 * Extracts an iCalendar object from a message by locating a {@code text/calendar} part, whether it
 * is an attachment, an inline body part, or the whole single-part message body.
 */
public class CalendarAttachmentProcessor implements Function<MimeMessage, Optional<Calendar>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarAttachmentProcessor.class);

    private static final String CALENDAR_MIME_TYPE = "text/calendar";

    @Override
    public Optional<Calendar> apply(MimeMessage mimeMessage) {
        try {
            return fromPart(mimeMessage);
        } catch (IOException | MessagingException e) {
            LOGGER.error("Unexpected error", e);
        }
        return Optional.empty();
    }

    private Optional<Calendar> fromPart(Part part) throws IOException, MessagingException {
        if (part.isMimeType(CALENDAR_MIME_TYPE)) {
            return parse(part);
        }
        Object content = part.getContent();
        if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            for (int i = 0; i < multipart.getCount(); i++) {
                Optional<Calendar> calendar = fromPart(multipart.getBodyPart(i));
                if (calendar.isPresent()) {
                    return calendar;
                }
            }
        }
        return Optional.empty();
    }

    private Optional<Calendar> parse(Part part) {
        try (InputStream in = part.getInputStream()) {
            return Optional.of(new CalendarBuilder().build(in));
        } catch (IOException | MessagingException e) {
            LOGGER.error("Unexpected error reading calendar part", e);
        } catch (ParserException e) {
            LOGGER.error("Invalid calendar content", e);
        }
        return Optional.empty();
    }
}
