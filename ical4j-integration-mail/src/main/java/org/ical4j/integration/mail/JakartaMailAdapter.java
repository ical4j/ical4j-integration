package org.ical4j.integration.mail;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import org.ical4j.integration.ChannelAdapter;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A channel adapter implementation capable of sending and receiving content via email.
 */
public class JakartaMailAdapter<T> implements ChannelAdapter<T> {

    private final Session session;

    private final MimeMessageBuilder<T> builder;

    private final MimeMessageParser<T> parser;

    public JakartaMailAdapter(MimeMessageBuilder<T> builder, MimeMessageParser<T> parser) {
        this(null, builder, parser);
    }

    public JakartaMailAdapter(Session session, MimeMessageBuilder<T> builder, MimeMessageParser<T> parser) {
        this.session = session;
        this.builder = builder;
        this.parser = parser;
    }

    @Override
    public boolean send(Supplier<T> supplier) {
        try {
            Transport.send(builder.withContent(supplier.get()).build());
            return true;
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean receive(Consumer<T> consumer, long timeout, boolean autoExpunge) {
        try {
            Store store = session.getStore();
            Folder inbox = store.getDefaultFolder();
            if (inbox.hasNewMessages()) {
                for (int i = 0; i < inbox.getNewMessageCount(); i++) {
                    Message message = inbox.getMessage(i);
                    if (message.isMimeType("text/calendar")) {
                        consumer.accept(parser.parse((MimeMessage) message).get());
                        return true;
                    }
                }
            }
            return false;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
