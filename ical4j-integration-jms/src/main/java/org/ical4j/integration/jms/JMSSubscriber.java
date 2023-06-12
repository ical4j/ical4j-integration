package org.ical4j.integration.jms;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.Session;
import org.ical4j.integration.AbstractChannelSubscriber;

public class JMSSubscriber<T> extends AbstractChannelSubscriber<T> implements MessageListener {

    private final Class<T> type;

    public JMSSubscriber(Class<T> type, Session session) throws JMSException {
        this.type = type;

        session.setMessageListener(this);
    }

    @Override
    public void onMessage(Message message) {
        try {
            publishObject(message.getBody(type));
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
