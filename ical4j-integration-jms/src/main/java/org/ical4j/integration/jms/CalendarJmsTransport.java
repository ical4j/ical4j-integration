package org.ical4j.integration.jms;

import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.CalendarConsumer;
import org.ical4j.integration.CalendarProducer;
import org.ical4j.integration.FailedDeliveryException;

import javax.jms.*;
import java.io.IOException;
import java.util.Optional;

public class CalendarJmsTransport implements CalendarProducer, CalendarConsumer {

    private final Session session;

    private final Destination destination;

    public CalendarJmsTransport(Session session, Destination destination) {
        this.session = session;
        this.destination = destination;
    }

    @Override
    public Optional<Calendar> poll(long timeout) throws IOException {
        try {
            MessageConsumer consumer = session.createConsumer(destination);
            Message message = consumer.receive(timeout);
            //TODO: parse and return calendar object
            return Optional.empty();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void send(Calendar calendar) throws FailedDeliveryException {
        try {
            MessageProducer producer = session.createProducer(destination);
            producer.send(new MessageBuilder().session(session).build());
        } catch (JMSException e) {
            throw new FailedDeliveryException(e);
        }
    }
}
