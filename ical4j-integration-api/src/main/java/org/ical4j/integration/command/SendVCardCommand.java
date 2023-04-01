package org.ical4j.integration.command;

import net.fortuna.ical4j.vcard.VCard;
import org.ical4j.integration.Message;
import picocli.CommandLine;

@CommandLine.Command(name = "send-card", description = "Send a vCard object to producer recipients")
public class SendVCardCommand extends AbstractEndpointCommand<VCard, Boolean> {

    private VCard card;

    public SendVCardCommand withCard(VCard card) {
        this.card = card;
        return this;
    }

    @Override
    public void run() {
        getConsumer().accept(getEndpoint().send(() -> new Message<>(null, card)));
    }
}
