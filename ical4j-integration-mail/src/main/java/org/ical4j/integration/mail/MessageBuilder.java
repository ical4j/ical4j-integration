package org.ical4j.integration.mail;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * A {@link MimeMessage} builder that is compliant with the iCalendar iMIP standard.
 * See: <a href="https://tools.ietf.org/html/rfc6047">rfc6047 (iMIP)</a>
 */
public class MessageBuilder {

    private org.ical4j.integration.Message<Calendar> calendar;

    private Session session;

    private MessageTemplate template;

    public MessageBuilder withSession(Session session) {
        this.session = session;
        return this;
    }

    public MessageBuilder withCalendar(org.ical4j.integration.Message<Calendar> calendar) {
        this.calendar = calendar;
        return this;
    }

    public MessageBuilder withTemplate(MessageTemplate template) {
        this.template = template;
        return this;
    }

    public Message build() throws MessagingException, IOException {
//        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        StringWriter sout = new StringWriter();
        CalendarOutputter calout = new CalendarOutputter();
        calout.output(calendar.getBody(), sout);

        MimeBodyPart calpart = new MimeBodyPart();
        calpart.setDisposition(MimeBodyPart.ATTACHMENT);
        calpart.setFileName("calendar.ics");
        calpart.setContent(sout.toString(), calendar.getBody().getContentType(StandardCharsets.US_ASCII));

        MimeBodyPart textpart = new MimeBodyPart();
        textpart.setContent(template.getTextBody(), "text/plain; charset=UTF-8");

        MimeMultipart body = new MimeMultipart();
        body.addBodyPart(calpart);
        body.addBodyPart(textpart);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(template.getFromAddress());
        message.addRecipients(Message.RecipientType.TO, String.join(";", template.getToAddresses()));
        message.setSubject(template.getSubject());
        message.setContent(body);
        return message;
    }
}
