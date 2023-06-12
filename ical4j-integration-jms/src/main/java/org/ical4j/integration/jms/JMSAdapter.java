package org.ical4j.integration.jms;

import jakarta.jms.*;
import org.ical4j.integration.ChannelAdapter;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class JMSAdapter<T> implements ChannelAdapter<T> {

    private final Session session;

    private final Destination destination;

    private final MessageBuilder messageBuilder;

    private MessageParser<T> messageParser;

    public JMSAdapter(Session session, Destination destination, MessageBuilder messageBuilder) {
        this.session = session;
        this.destination = destination;
        this.messageBuilder = messageBuilder;
    }

    @Override
    public boolean send(Supplier<T> supplier) {
        try {
            MessageProducer producer = session.createProducer(destination);
            producer.send(messageBuilder.build());
        } catch (JMSException e) {
//            throw new FailedDeliveryException(e);
        }
        return false;
    }

    @Override
    public boolean receive(Consumer<T> consumer, long timeout, boolean autoExpunge) {
        try {
            MessageConsumer messageConsumer = session.createConsumer(destination);
            consumer.accept(messageParser.parse(messageConsumer.receive(timeout)).get());
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
