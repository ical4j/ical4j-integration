package org.ical4j.integration.mail.builder;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.StyledDescription;
import net.fortuna.ical4j.model.property.Summary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Function;

public class EventAttachmentBuilder implements Function<Calendar, Optional<MimeMessage>> {

    Logger LOGGER = LoggerFactory.getLogger(EventAttachmentBuilder.class);

    @Override
    public Optional<MimeMessage> apply(Calendar calendar) {
        try {
            Optional<VEvent> event = calendar.getComponent("VEVENT");
            if (event.isPresent()) {
                Summary summary = event.get().getRequiredProperty("SUMMARY");
                Optional<Description> description = event.get().getProperty("DESCRIPTION");
                Optional<StyledDescription> styledDescription = event.get().getProperty("STYLED-DESCRIPTION");
//                List<Participant> participantList = event.get().getComponents("PARTICIPANT");

                StringWriter sout = new StringWriter();
                CalendarOutputter calout = new CalendarOutputter();
                calout.output(calendar, sout);

                MimeBodyPart calpart = new MimeBodyPart();
                calpart.setDisposition(MimeBodyPart.ATTACHMENT);
                calpart.setFileName("calendar.ics");
                calpart.setContent(sout.toString(), calendar.getContentType(StandardCharsets.US_ASCII));

                MimeBodyPart textpart = new MimeBodyPart();
                if (description.isPresent()) {
                    textpart.setContent(description.get().getValue(), "text/plain; charset=UTF-8");
                } else {
                    textpart.setContent(summary.getValue(), "text/plain; charset=UTF-8");
                }

                MimeMultipart body = new MimeMultipart();
                body.addBodyPart(calpart);
                body.addBodyPart(textpart);

                if (styledDescription.isPresent()) {
                    MimeBodyPart htmlpart = new MimeBodyPart();
                    htmlpart.setContent(styledDescription.get().getValue(), "text/html; charset=UTF-8");
                    body.addBodyPart(htmlpart);
                }

                MimeMessage message = new MimeMessage(Session.getDefaultInstance(null));
//                message.setFrom(template.getFromAddress());
//                message.addRecipients(Message.RecipientType.TO, String.join(";", template.getToAddresses()));
                message.setSubject(summary.getValue());
                message.setContent(body);
                return Optional.of(message);
            }
        } catch (IOException | MessagingException e) {
            LOGGER.error("Unexpected error", e);
        }
        return Optional.empty();
    }
}
