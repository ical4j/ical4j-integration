package org.ical4j.integration.command;

import net.fortuna.ical4j.vcard.VCard;
import org.ical4j.integration.VCardConsumer;
import picocli.CommandLine;

@CommandLine.Command(name = "receive-cards", description = "Receive vCard objects from mail stream")
public class ReceiveVCardCommand implements Runnable {

    private final VCardConsumer consumer;

    private Iterable<VCard> cards;

    public ReceiveVCardCommand(VCardConsumer consumer) {
        this.consumer = consumer;
    }

    public Iterable<VCard> getCards() {
        return cards;
    }

    @Override
    public void run() {

    }
}
