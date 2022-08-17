package org.ical4j.integration.jms;

import jakarta.jms.Message;
import java.util.Optional;

public interface MessageParser<T> {

    Optional<T> parse(Message message);
}
