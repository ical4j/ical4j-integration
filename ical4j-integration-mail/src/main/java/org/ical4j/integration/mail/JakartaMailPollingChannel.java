package org.ical4j.integration.mail;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.IngressChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Poll a mailbox for available calendar objects.
 */
public class JakartaMailPollingChannel implements IngressChannel<Calendar> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JakartaMailPollingChannel.class);

    private final Session session;

    private final Function<MimeMessage, Optional<Calendar>> messageProcessor;

    public JakartaMailPollingChannel(Session session, Function<MimeMessage, Optional<Calendar>> messageProcessor) {
        this.session = session;
        this.messageProcessor = messageProcessor;
    }

    @Override
    public boolean poll(Consumer<Calendar> consumer, long timeout) {
        try {
            Store store = session.getStore();
            Folder inbox = store.getDefaultFolder();
            if (inbox.hasNewMessages()) {
                for (int i = 0; i < inbox.getNewMessageCount(); i++) {
                    Message message = inbox.getMessage(i);
                    if (message instanceof MimeMessage) {
                        messageProcessor.apply((MimeMessage) message).ifPresent(consumer);
                    }
                }
            }
        } catch (MessagingException e) {
            LOGGER.error("Error retrieving messages", e);
        }
        return false;
    }
}
