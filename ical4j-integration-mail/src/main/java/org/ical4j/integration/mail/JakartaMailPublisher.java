package org.ical4j.integration.mail;

import jakarta.mail.*;
import jakarta.mail.event.ConnectionEvent;
import jakarta.mail.event.ConnectionListener;
import jakarta.mail.event.MessageCountEvent;
import jakarta.mail.event.MessageCountListener;
import jakarta.mail.internet.MimeMessage;
import net.fortuna.ical4j.model.Calendar;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Function;

public class JakartaMailPublisher extends SubmissionPublisher<Calendar>
        implements MessageCountListener, ConnectionListener {

    private final Function<MimeMessage, Optional<Calendar>> messageProcessor;

    public JakartaMailPublisher(Session session, Function<MimeMessage, Optional<Calendar>> messageProcessor,
                                URLName...folders) {
        this.messageProcessor = messageProcessor;
        try {
            session.getStore().addConnectionListener(this);
            Arrays.stream(folders).forEach(f -> {
                try {
                    session.getFolder(f).addMessageCountListener(this);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void messagesAdded(MessageCountEvent e) {
        for (Message message : e.getMessages()) {
            if (message instanceof MimeMessage) {
                messageProcessor.apply((MimeMessage) message).ifPresent(this::submit);
            }
        }
    }

    @Override
    public void messagesRemoved(MessageCountEvent e) {

    }

    @Override
    public void opened(ConnectionEvent e) {

    }

    @Override
    public void disconnected(ConnectionEvent e) {

    }

    @Override
    public void closed(ConnectionEvent e) {
        this.close();
    }
}
