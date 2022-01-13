package org.ical4j.integration.mail;

import javax.mail.Message;

public interface MessageParser<T> {

    T parse(Message message);
}
