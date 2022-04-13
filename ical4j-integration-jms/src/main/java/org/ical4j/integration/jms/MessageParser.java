package org.ical4j.integration.jms;

import javax.jms.Message;
import java.util.Optional;

public interface MessageParser<T> {

    Optional<T> parse(Message message);
}
