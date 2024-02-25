package org.ical4j.integration.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import org.ical4j.integration.EgressChannel;
import org.ical4j.integration.flow.ChannelPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class JakartaMailSMTPChannel<T> extends ChannelPublisher<MimeMessage> implements EgressChannel<T, MimeMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JakartaMailSMTPChannel.class);

    private final Function<T, Optional<MimeMessage>> messageBuilder;

    public JakartaMailSMTPChannel(Function<T, Optional<MimeMessage>> messageBuilder) {
        this.messageBuilder = messageBuilder;
    }

    @Override
    public boolean send(Supplier<T> supplier) {
        try {
            Optional<MimeMessage> message = messageBuilder.apply(supplier.get());
            if (message.isPresent()) {
                Transport.send(message.get());
                submit(message.get());
                return true;
            }
        } catch (MessagingException e) {
            LOGGER.error("Error sending payload", e);
        }
        return false;
    }
}
