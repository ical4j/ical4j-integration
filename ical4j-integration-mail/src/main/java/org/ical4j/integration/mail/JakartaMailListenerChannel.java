package org.ical4j.integration.mail;

import jakarta.mail.*;
import jakarta.mail.event.ConnectionEvent;
import jakarta.mail.event.ConnectionListener;
import jakarta.mail.event.MessageCountEvent;
import jakarta.mail.event.MessageCountListener;
import jakarta.mail.internet.MimeMessage;
import org.ical4j.integration.IngressChannel;
import org.ical4j.integration.flow.ChannelPublisher;

import java.util.Arrays;

public class JakartaMailListenerChannel extends ChannelPublisher<MimeMessage>
        implements IngressChannel<MimeMessage>, MessageCountListener, ConnectionListener {

    public JakartaMailListenerChannel(Session session, URLName...folders) {
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
                submit((MimeMessage) message);
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
