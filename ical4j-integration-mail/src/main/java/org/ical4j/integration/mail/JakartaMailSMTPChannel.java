package org.ical4j.integration.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.EgressChannel;
import org.ical4j.integration.mail.builder.EventAttachmentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Sends a calendar over SMTP. Messages are built via the supplied message builder and transported
 * using the channel's {@link Session}, ensuring construction and transport share the same session.
 */
public class JakartaMailSMTPChannel implements EgressChannel<Calendar> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JakartaMailSMTPChannel.class);

    private final Session session;

    private final Function<Calendar, Optional<MimeMessage>> messageBuilder;

    /**
     * Create a channel using the default {@link EventAttachmentBuilder} bound to the given session.
     */
    public JakartaMailSMTPChannel(Session session) {
        this(session, new EventAttachmentBuilder(session));
    }

    public JakartaMailSMTPChannel(Session session, Function<Calendar, Optional<MimeMessage>> messageBuilder) {
        this.session = session;
        this.messageBuilder = messageBuilder;
    }

    @Override
    public boolean send(Supplier<Calendar> supplier) {
        Optional<MimeMessage> message = messageBuilder.apply(supplier.get());
        if (message.isEmpty()) {
            LOGGER.info("No message produced for calendar; nothing to send");
            return false;
        }
        MimeMessage msg = message.get();
        Transport transport = null;
        try {
            transport = session.getTransport();
            transport.connect();
            transport.sendMessage(msg, msg.getAllRecipients());
            return true;
        } catch (MessagingException e) {
            LOGGER.error("Error sending payload", e);
            return false;
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    LOGGER.debug("Error closing transport", e);
                }
            }
        }
    }
}
