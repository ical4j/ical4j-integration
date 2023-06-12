package org.ical4j.integration.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;

/**
 * Implementors support MIME message creation from a specified content type.
 * @param <T> the supported content type
 */
public interface MimeMessageBuilder<T> {

    MimeMessageBuilder<T> withContent(T content);

    MimeMessage build() throws MessagingException, IOException;
}
