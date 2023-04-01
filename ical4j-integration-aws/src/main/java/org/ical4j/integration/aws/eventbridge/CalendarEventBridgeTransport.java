package org.ical4j.integration.aws.eventbridge;

import com.amazonaws.services.eventbridge.AmazonEventBridge;
import com.amazonaws.services.eventbridge.model.PutEventsRequest;
import com.amazonaws.services.eventbridge.model.PutEventsRequestEntry;
import com.amazonaws.services.eventbridge.model.PutEventsResult;
import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.Message;
import org.ical4j.integration.MessageProducer;

import java.util.function.Supplier;

public class CalendarEventBridgeTransport implements MessageProducer<Calendar> {

    private final AmazonEventBridge client;

    public CalendarEventBridgeTransport(AmazonEventBridge client) {
        this.client = client;
    }

    @Override
    public boolean send(Supplier<Message<Calendar>> calendar) {
        PutEventsRequestEntry requestEntry = new PutEventsRequestEntry();

        PutEventsRequest request = new PutEventsRequest().withEntries(requestEntry);

        PutEventsResult result = client.putEvents(request);

        return true;
    }
}
