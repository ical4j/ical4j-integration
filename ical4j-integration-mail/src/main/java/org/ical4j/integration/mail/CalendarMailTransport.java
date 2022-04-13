package org.ical4j.integration.mail;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.validate.ITIPValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import org.ical4j.integration.CalendarConsumer;
import org.ical4j.integration.CalendarProducer;
import org.ical4j.integration.FailedDeliveryException;

import javax.mail.*;
import java.io.IOException;
import java.util.Optional;

public class CalendarMailTransport implements CalendarProducer, CalendarConsumer {

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
    public void send(Calendar calendar) throws FailedDeliveryException {
        ITIPValidator validator = new ITIPValidator();
        ValidationResult result = validator.validate(calendar);
        if (result.hasErrors()) {
            throw new ValidationException(result.toString());
        }
        try {
            Message message = new MessageBuilder().withSession(session)
                    .withTemplate(messageTemplate)
                    .withCalendar(calendar).build();
            Transport.send(message);
        } catch (MessagingException | IOException e) {
            throw new FailedDeliveryException(e);
        }
    }

    @Override
    public Optional<Calendar> poll(long timeout) {
        try {
            Store store = session.getStore();
            Folder inbox = store.getDefaultFolder();
            if (inbox.hasNewMessages()) {
                for (int i = 0; i < inbox.getNewMessageCount(); i++) {
                    Message message = inbox.getMessage(i);
                    if (message.isMimeType("text/calendar")) {
                        return messageParser.parse(message);
                    }
                }
            }
            return Optional.empty();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
