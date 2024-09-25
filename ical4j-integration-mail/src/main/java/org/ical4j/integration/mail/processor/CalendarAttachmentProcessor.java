package org.ical4j.integration.mail.processor;

import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

public class CalendarAttachmentProcessor implements Function<MimeMessage, Optional<Calendar>> {

    Logger LOGGER = LoggerFactory.getLogger(CalendarAttachmentProcessor.class);

    @Override
    public Optional<Calendar> apply(MimeMessage mimeMessage) {
        try {
            Multipart multiPart = (Multipart) mimeMessage.getContent();
            int numberOfParts = multiPart.getCount();
            for (int partCount = 0; partCount < numberOfParts; partCount++) {
                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    part.writeTo(buffer);
                    Calendar calendar = new CalendarBuilder().build(new ByteArrayInputStream(buffer.toByteArray()));
                }
            }
        } catch (IOException | MessagingException e) {
            LOGGER.error("Unexpected error", e);
        } catch (ParserException e) {
            LOGGER.error("Invalid attachment", e);
        }
        return Optional.empty();
    }
}
