package org.ical4j.integration.mail;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.event.MessageCountEvent;
import jakarta.mail.event.MessageCountListener;
import jakarta.mail.internet.MimeMessage;
import org.ical4j.integration.AbstractChannelSubscriber;

import java.util.Arrays;
import java.util.Optional;

public class JakartaMailSubscriber<T> extends AbstractChannelSubscriber<T> implements MessageCountListener {

    private final MimeMessageParser<T> messageParser;

    public JakartaMailSubscriber(MimeMessageParser<T> messageParser, Folder...folders) {
        this.messageParser = messageParser;

        Arrays.stream(folders).forEach(f -> f.addMessageCountListener(this));
    }

    @Override
    public void messagesAdded(MessageCountEvent e) {
        for (Message message : e.getMessages()) {
            Optional<T> calendar = messageParser.parse((MimeMessage) message);
            calendar.ifPresent(this::publishObject);
        }
    }

    @Override
    public void messagesRemoved(MessageCountEvent e) {

    }
}
