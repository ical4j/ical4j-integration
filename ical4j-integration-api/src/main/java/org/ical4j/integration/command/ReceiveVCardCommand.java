package org.ical4j.integration.command;

import net.fortuna.ical4j.vcard.VCard;
import org.ical4j.integration.VCardConsumer;
import picocli.CommandLine;

import java.io.IOException;
import java.util.Optional;

@CommandLine.Command(name = "receive-cards", description = "Receive vCard objects from consumer stream")
public class ReceiveVCardCommand implements Runnable {

    private final VCardConsumer consumer;

    private long timeout;

    private Optional<VCard> card;

    public ReceiveVCardCommand(VCardConsumer consumer) {
        this.consumer = consumer;
    }

    public Optional<VCard> getCard() {
        return card;
    }

    @Override
    public void run() {
        try {
            this.card = consumer.poll(timeout);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
