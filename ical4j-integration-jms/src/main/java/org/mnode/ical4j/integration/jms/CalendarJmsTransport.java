package org.mnode.ical4j.integration.jms;

import net.fortuna.ical4j.model.Calendar;
import org.mnode.ical4j.integration.CalendarConsumer;
import org.mnode.ical4j.integration.CalendarProducer;
import org.mnode.ical4j.integration.FailedDeliveryException;

import javax.jms.*;

public class CalendarJmsTransport implements CalendarProducer, CalendarConsumer {

    private final Session session;

    private final Destination destination;

    public CalendarJmsTransport(Session session, Destination destination) {
        this.session = session;
        this.destination = destination;
    }

    @Override
    public Calendar poll(long timeout) throws FailedDeliveryException {
        MessageConsumer consumer = null;
        try {
            consumer = session.createConsumer(destination);
            Message message = consumer.receive();
            //TODO: parse and return calendar object
            return null;
        } catch (JMSException e) {
            throw new FailedDeliveryException(e);
        }
    }

    @Override
    public void send(Calendar calendar) throws FailedDeliveryException {
        MessageProducer producer = null;
        try {
            producer = session.createProducer(destination);
            producer.send(new MessageBuilder().session(session).build());
        } catch (JMSException e) {
            throw new FailedDeliveryException(e);
        }
    }
}
