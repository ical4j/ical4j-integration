package org.ical4j.integration.jms;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;

public class MessageBuilder {

    private Session session;

    MessageBuilder session(Session session) {
        this.session = session;
        return this;
    }
    
    Message build() throws JMSException {
        return session.createMessage();
    }
}
