package org.ical4j.integration.mail;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.util.Calendars;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link MimeMessage} builder that is compliant with the iCalendar iMIP standard.
 * See: <a href="https://tools.ietf.org/html/rfc6047">rfc6047 (iMIP)</a>
 */
public class MessageBuilder {

    private Calendar calendar;

    private String fromAddress;

    private List<String> toAddresses = new ArrayList<>();

    private String subject;

    private String textBody;

    private Session session;

    public MessageBuilder withSession(Session session) {
        this.session = session;
        return this;
    }

    public MessageBuilder withCalendar(Calendar calendar) {
        this.calendar = calendar;
        return this;
    }

    public Message build() throws MessagingException, IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        CalendarOutputter calout = new CalendarOutputter();
        calout.output(calendar, bout);

        MimeBodyPart calpart = new MimeBodyPart();
        calpart.setContent(bout.toByteArray(),
                Calendars.getContentType(calendar, StandardCharsets.US_ASCII));

        MimeBodyPart textpart = new MimeBodyPart();
        textpart.setContent(textBody, "text/plain; charset=UTF-8");

        MimeMultipart body = new MimeMultipart();
        body.addBodyPart(calpart);
        body.addBodyPart(textpart);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(fromAddress);
        message.setSubject(subject);
        message.setContent(body);
        return message;
    }
}
