package org.ical4j.integration.command;

import net.fortuna.ical4j.vcard.VCard;
import picocli.CommandLine;

import java.util.function.Consumer;

@CommandLine.Command(name = "receive-card", description = "Receive vCard objects from consumer stream")
public class ReceiveVCardCommand extends AbstractConsumerCommand<VCard, VCard> {

    private long timeout;

    private boolean autoExpunge;

    public ReceiveVCardCommand(Consumer<VCard> consumer) {
        super(consumer);
    }

    @Override
    public void run() {
        getEndpoint().receive(getConsumer(), timeout, autoExpunge);
    }
}
