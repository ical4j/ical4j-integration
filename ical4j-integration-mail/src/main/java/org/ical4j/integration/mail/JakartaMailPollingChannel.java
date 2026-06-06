package org.ical4j.integration.mail;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeMessage;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import org.ical4j.integration.IngressChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Poll a mailbox for available calendar objects. Connects the store, opens the {@code INBOX}, and
 * delivers parsed calendars to the consumer. Supports removing processed messages via
 * {@code autoExpunge} or {@link #expunge(String)}.
 */
public class JakartaMailPollingChannel implements IngressChannel<Calendar> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JakartaMailPollingChannel.class);

    private static final String INBOX = "INBOX";

    private final Session session;

    private final Function<MimeMessage, Optional<Calendar>> messageProcessor;

    public JakartaMailPollingChannel(Session session, Function<MimeMessage, Optional<Calendar>> messageProcessor) {
        this.session = session;
        this.messageProcessor = messageProcessor;
    }

    @Override
    public boolean poll(Consumer<Calendar> consumer, long timeout) {
        return poll(consumer, timeout, false);
    }

    @Override
    public boolean poll(Consumer<Calendar> consumer, long timeout, boolean autoExpunge) {
        Store store = null;
        Folder inbox = null;
        boolean consumed = false;
        try {
            store = session.getStore();
            store.connect();
            inbox = store.getFolder(INBOX);
            inbox.open(autoExpunge ? Folder.READ_WRITE : Folder.READ_ONLY);

            awaitMessages(inbox, timeout);

            for (Message message : inbox.getMessages()) {
                if (message instanceof MimeMessage) {
                    Optional<Calendar> calendar = messageProcessor.apply((MimeMessage) message);
                    if (calendar.isPresent()) {
                        consumer.accept(calendar.get());
                        consumed = true;
                        if (autoExpunge) {
                            message.setFlag(Flags.Flag.DELETED, true);
                        }
                    }
                }
            }
        } catch (MessagingException e) {
            LOGGER.error("Error retrieving messages", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            closeQuietly(inbox, autoExpunge);
            closeQuietly(store);
        }
        return consumed;
    }

    @Override
    public boolean expunge(String uid) {
        Store store = null;
        Folder inbox = null;
        boolean found = false;
        try {
            store = session.getStore();
            store.connect();
            inbox = store.getFolder(INBOX);
            inbox.open(Folder.READ_WRITE);
            for (Message message : inbox.getMessages()) {
                if (message instanceof MimeMessage) {
                    Optional<Calendar> calendar = messageProcessor.apply((MimeMessage) message);
                    if (calendar.isPresent() && matchesUid(calendar.get(), uid)) {
                        message.setFlag(Flags.Flag.DELETED, true);
                        found = true;
                    }
                }
            }
        } catch (MessagingException e) {
            LOGGER.error("Error expunging message", e);
        } finally {
            closeQuietly(inbox, true);
            closeQuietly(store);
        }
        return found;
    }

    /**
     * Wait up to {@code timeout} seconds for at least one message to be available.
     */
    private void awaitMessages(Folder inbox, long timeout) throws MessagingException, InterruptedException {
        long deadline = System.currentTimeMillis() + Math.max(0, timeout) * 1000L;
        while (inbox.getMessageCount() == 0 && System.currentTimeMillis() < deadline) {
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0) {
                break;
            }
            Thread.sleep(Math.min(500L, remaining));
        }
    }

    private boolean matchesUid(Calendar calendar, String uid) {
        for (CalendarComponent component : calendar.getComponents()) {
            Optional<Property> property = component.getProperty(Property.UID);
            if (property.isPresent() && uid.equals(property.get().getValue())) {
                return true;
            }
        }
        return false;
    }

    private void closeQuietly(Folder folder, boolean expunge) {
        if (folder != null && folder.isOpen()) {
            try {
                folder.close(expunge);
            } catch (MessagingException e) {
                LOGGER.debug("Error closing folder", e);
            }
        }
    }

    private void closeQuietly(Store store) {
        if (store != null && store.isConnected()) {
            try {
                store.close();
            } catch (MessagingException e) {
                LOGGER.debug("Error closing store", e);
            }
        }
    }
}
