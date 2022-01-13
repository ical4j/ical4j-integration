package org.ical4j.integration.http;

import com.rometools.rome.io.FeedException;
import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.CalendarConsumer;
import org.ical4j.integration.CalendarProducer;
import org.ical4j.integration.FailedDeliveryException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * HTTP implementation of a calendar transport.
 */
public class CalendarHttpTransport implements CalendarProducer, CalendarConsumer {

    private final HttpSupport httpSupport;

    private final String method;

    private final String path;

    public CalendarHttpTransport(HttpSupport httpSupport, String method, String path) {
        this.httpSupport = httpSupport;
        this.method = method;
        this.path = path;
    }

    @Override
    public void send(Calendar calendar) throws FailedDeliveryException {
        switch (method) {
            case "POST":
                httpSupport.post(path, calendar);
                break;
            case "PUT":
                httpSupport.put(path, calendar);
                break;
            default:
                throw new IllegalArgumentException("Unsupported method");
        }
    }

    @Override
    public Calendar poll(long timeout) throws FailedDeliveryException {
        try {
            switch (method) {
                case "GET":
                    return httpSupport.get(path);
                case "POST":
                    return httpSupport.post(path, "");
                default:
                    throw new IllegalArgumentException("Unsupported method");
            }
        } catch (IOException | FeedException | URISyntaxException | ParseException e) {
            throw new FailedDeliveryException(e);
        }
    }
}
