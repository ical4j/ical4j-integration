package org.ical4j.integration.mail;

import com.sun.mail.imap.IMAPFolder;
import jakarta.mail.Folder;
import jakarta.mail.FolderClosedException;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.event.MessageCountEvent;
import jakarta.mail.event.MessageCountListener;
import jakarta.mail.internet.MimeMessage;
import net.fortuna.ical4j.model.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * A reactive {@link java.util.concurrent.Flow.Publisher} that emits calendar objects as matching
 * messages arrive in the monitored folders. The publisher connects the mail store, opens each
 * monitored folder, and maintains the connection via an IMAP IDLE keepalive (falling back to
 * periodic polling for non-IMAP folders) so that message-count events continue to fire.
 */
public class JakartaMailPublisher extends SubmissionPublisher<Calendar>
        implements MessageCountListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JakartaMailPublisher.class);

    private static final long POLL_INTERVAL_MS = 1000L;

    private final Function<MimeMessage, Optional<Calendar>> messageProcessor;

    private final Store store;

    private final List<Folder> folders = new ArrayList<>();

    private final ExecutorService keepAliveExecutor;

    private final AtomicBoolean running = new AtomicBoolean(true);

    public JakartaMailPublisher(Session session, Function<MimeMessage, Optional<Calendar>> messageProcessor,
                                String... folderNames) {
        this.messageProcessor = messageProcessor;
        try {
            this.store = session.getStore();
            store.connect();
            this.keepAliveExecutor = Executors.newCachedThreadPool();
            for (String name : folderNames) {
                Folder folder = store.getFolder(name);
                folder.open(Folder.READ_ONLY);
                folder.addMessageCountListener(this);
                folders.add(folder);
                keepAliveExecutor.submit(() -> keepAlive(folder));
            }
        } catch (MessagingException e) {
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
        // no-op
    }

    @Override
    public void close() {
        running.set(false);
        for (Folder folder : folders) {
            try {
                if (folder.isOpen()) {
                    folder.close(false);
                }
            } catch (MessagingException e) {
                LOGGER.debug("Error closing folder", e);
            }
        }
        keepAliveExecutor.shutdownNow();
        try {
            if (store.isConnected()) {
                store.close();
            }
        } catch (MessagingException e) {
            LOGGER.debug("Error closing store", e);
        }
        super.close();
    }

    /**
     * Keep the folder connection live so new-message events are delivered. For IMAP this issues
     * {@code IDLE}; for other providers it periodically refreshes the message count.
     */
    private void keepAlive(Folder folder) {
        while (running.get() && folder.isOpen()) {
            try {
                if (folder instanceof IMAPFolder) {
                    ((IMAPFolder) folder).idle();
                } else {
                    Thread.sleep(POLL_INTERVAL_MS);
                    folder.getMessageCount();
                }
            } catch (FolderClosedException e) {
                break;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (MessagingException e) {
                if (running.get()) {
                    LOGGER.warn("Keepalive stopped for folder", e);
                }
                break;
            }
        }
    }
}
