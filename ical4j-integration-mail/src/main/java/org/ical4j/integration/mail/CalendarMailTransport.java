package org.ical4j.integration.mail;

import jakarta.mail.*;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.validate.ITIPValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import org.ical4j.integration.MessageConsumer;
import org.ical4j.integration.MessageProducer;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CalendarMailTransport implements MessageProducer<Calendar>, MessageConsumer<Calendar> {

    private final Session session;

    private final MessageParser<Calendar> messageParser;

    private MessageTemplate messageTemplate;

    public CalendarMailTransport(Session session, MessageParser<Calendar> messageParser) {
        this.session = session;
        this.messageParser = messageParser;
    }

    public CalendarMailTransport withMessageTemplate(MessageTemplate messageTemplate) {
        this.messageTemplate = messageTemplate;
        return this;
    }

    @Override
    public boolean send(Supplier<org.ical4j.integration.Message<Calendar>> calendar) {
        ITIPValidator validator = new ITIPValidator();
        ValidationResult result = validator.validate(calendar.get().getBody());
        if (result.hasErrors()) {
            throw new ValidationException(result.toString());
        }
        try {
            Message message = new MessageBuilder().withSession(session)
                    .withTemplate(messageTemplate)
                    .withCalendar(calendar.get()).build();
            Transport.send(message);
            return true;
        } catch (MessagingException | IOException e) {
//            throw new FailedDeliveryException(e);
        }
        return false;
    }

    @Override
    public boolean receive(Consumer<Calendar> consumer, long timeout, boolean autoExpunge) {
        try {
            Store store = session.getStore();
            Folder inbox = store.getDefaultFolder();
            if (inbox.hasNewMessages()) {
                for (int i = 0; i < inbox.getNewMessageCount(); i++) {
                    Message message = inbox.getMessage(i);
                    if (message.isMimeType("text/calendar")) {
                        consumer.accept(messageParser.parse(message).get());
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
