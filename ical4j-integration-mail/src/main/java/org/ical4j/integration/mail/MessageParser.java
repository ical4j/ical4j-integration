package org.ical4j.integration.mail;

import jakarta.mail.Message;

import java.util.Optional;

public interface MessageParser<T> {

    Optional<T> parse(Message message);
}
