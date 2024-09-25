package org.ical4j.integration.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.EgressChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

public class JakartaMailSMTPChannel implements EgressChannel<Calendar> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JakartaMailSMTPChannel.class);

    private final Function<Calendar, Optional<MimeMessage>> messageBuilder;

    public JakartaMailSMTPChannel(Function<Calendar, Optional<MimeMessage>> messageBuilder) {
        this.messageBuilder = messageBuilder;
    }

    @Override
    public boolean send(Supplier<Calendar> supplier) {
        AtomicBoolean sent = new AtomicBoolean(false);
        messageBuilder.apply(supplier.get()).ifPresentOrElse(msg -> {
            try {
                Transport.send(msg);
                sent.set(true);
            } catch (MessagingException e) {
                LOGGER.error("Error sending payload", e);
            }
        }, () -> LOGGER.info(""));
        return sent.get();
    }
}
