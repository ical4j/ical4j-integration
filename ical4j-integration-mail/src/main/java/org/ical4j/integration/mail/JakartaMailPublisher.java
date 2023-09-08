package org.ical4j.integration.mail;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.event.ConnectionEvent;
import jakarta.mail.event.ConnectionListener;
import jakarta.mail.event.MessageCountEvent;
import jakarta.mail.event.MessageCountListener;
import jakarta.mail.internet.MimeMessage;
import org.ical4j.integration.AbstractChannelPublisher;

import java.util.Arrays;
import java.util.Optional;

public class JakartaMailPublisher<T> extends AbstractChannelPublisher<T> implements MessageCountListener,
        ConnectionListener {

    private final MimeMessageParser<T> messageParser;

    public JakartaMailPublisher(Session session, MimeMessageParser<T> messageParser, Folder...folders) {
        this.messageParser = messageParser;
//        session.getStore().addConnectionListener(this);
        Arrays.stream(folders).forEach(f -> f.addMessageCountListener(this));
    }

    @Override
    public void messagesAdded(MessageCountEvent e) {
        for (Message message : e.getMessages()) {
            Optional<T> calendar = messageParser.parse((MimeMessage) message);
            calendar.ifPresent(this::publish);
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
