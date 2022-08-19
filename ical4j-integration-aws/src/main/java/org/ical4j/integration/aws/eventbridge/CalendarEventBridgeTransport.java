package org.ical4j.integration.aws.eventbridge;

import com.amazonaws.services.eventbridge.AmazonEventBridge;
import com.amazonaws.services.eventbridge.model.PutEventsRequest;
import com.amazonaws.services.eventbridge.model.PutEventsRequestEntry;
import com.amazonaws.services.eventbridge.model.PutEventsResult;
import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.CalendarProducer;
import org.ical4j.integration.FailedDeliveryException;

public class CalendarEventBridgeTransport implements CalendarProducer {

    private final AmazonEventBridge client;

    public CalendarEventBridgeTransport(AmazonEventBridge client) {
        this.client = client;
    }

    @Override
    public void send(Calendar calendar) throws FailedDeliveryException {
        PutEventsRequestEntry requestEntry = new PutEventsRequestEntry();

        PutEventsRequest request = new PutEventsRequest().withEntries(requestEntry);

        PutEventsResult result = client.putEvents(request);
    }
}
