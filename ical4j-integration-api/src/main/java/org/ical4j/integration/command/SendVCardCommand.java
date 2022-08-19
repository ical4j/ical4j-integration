package org.ical4j.integration.command;

import net.fortuna.ical4j.vcard.VCard;
import org.ical4j.integration.FailedDeliveryException;
import org.ical4j.integration.VCardProducer;
import picocli.CommandLine;

@CommandLine.Command(name = "send-card", description = "Send a vCard object to producer recipients")
public class SendVCardCommand implements Runnable {

    private final VCardProducer producer;

    private VCard card;

    public SendVCardCommand(VCardProducer producer) {
        this.producer = producer;
    }

    public SendVCardCommand withCard(VCard card) {
        this.card = card;
        return this;
    }

    @Override
    public void run() {
        try {
            producer.send(card);
        } catch (FailedDeliveryException e) {
            throw new RuntimeException(e);
        }
    }
}
