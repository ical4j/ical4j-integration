package org.ical4j.integration.jms;

import javax.jms.Message;

public interface MessageParser<T> {

    T parse(Message message);
}
