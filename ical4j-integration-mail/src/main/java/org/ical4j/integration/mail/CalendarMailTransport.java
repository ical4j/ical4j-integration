package org.ical4j.integration.mail;

import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.CalendarConsumer;
import org.ical4j.integration.CalendarProducer;
import org.ical4j.integration.FailedDeliveryException;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import java.io.IOException;

public class CalendarMailTransport implements CalendarProducer, CalendarConsumer {

    private final Session session;

    public CalendarMailTransport(Session session) {
        this.session = session;
    }

    @Override
    public void send(Calendar calendar) throws FailedDeliveryException {
        try {
            Transport.send(new MessageBuilder().session(session).build());
        } catch (MessagingException | IOException e) {
            throw new FailedDeliveryException(e);
        }
    }

    @Override
    public Calendar poll(long timeout) {
        return null;
    }
}
