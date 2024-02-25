package org.ical4j.integration.mail;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import org.ical4j.integration.IngressChannel;
import org.ical4j.integration.flow.ChannelPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JakartaMailPollingChannel extends ChannelPublisher<MimeMessage> implements IngressChannel<MimeMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JakartaMailPollingChannel.class);

    private final Session session;

    public JakartaMailPollingChannel(Session session) {
        this.session = session;
    }

    @Override
    public boolean poll(long timeout, boolean autoExpunge) {
        try {
            Store store = session.getStore();
            Folder inbox = store.getDefaultFolder();
            if (inbox.hasNewMessages()) {
                for (int i = 0; i < inbox.getNewMessageCount(); i++) {
                    Message message = inbox.getMessage(i);
                    if (message instanceof MimeMessage) {
                        submit((MimeMessage) message);
                    }
                }
            }
        } catch (MessagingException e) {
            LOGGER.error("Error receiving payload", e);
        }
        return false;
    }
}
