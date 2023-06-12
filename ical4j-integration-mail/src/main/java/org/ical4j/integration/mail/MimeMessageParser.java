package org.ical4j.integration.mail;

import jakarta.mail.internet.MimeMessage;

import java.util.Optional;

public interface MimeMessageParser<T> {

    Optional<T> parse(MimeMessage message);
}
