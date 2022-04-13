package org.ical4j.integration.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

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
