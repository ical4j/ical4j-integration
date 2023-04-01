package org.ical4j.integration.http;

import com.rometools.rome.io.FeedException;
import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.Message;
import org.ical4j.integration.MessageConsumer;
import org.ical4j.integration.MessageProducer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * HTTP implementation of a calendar transport.
 */
public class CalendarHttpTransport implements MessageProducer<Calendar>, MessageConsumer<Calendar> {

    private final HttpSupport httpSupport;

    private final String method;

    private final String path;

    public CalendarHttpTransport(String path) {
        this(null, "GET", path);
    }

    public CalendarHttpTransport(HttpSupport httpSupport, String method, String path) {
        this.httpSupport = httpSupport;
        this.method = method;
        this.path = path;
    }

    @Override
    public boolean send(Supplier<Message<Calendar>> calendar) {
        switch (method) {
            case "POST":
                httpSupport.post(path, calendar.get().getBody());
                break;
            case "PUT":
                httpSupport.put(path, calendar.get().getBody());
                break;
            default:
                throw new IllegalArgumentException("Unsupported method");
        }
        return true;
    }

    @Override
    public boolean receive(Consumer<Calendar> consumer, long timeout, boolean autoExpunge) {
        try {
            switch (method) {
                case "GET":
                    consumer.accept(httpSupport.get(path));
                    return true;
                case "POST":
                    consumer.accept(httpSupport.post(path, ""));
                    return true;
                default:
                    throw new IllegalArgumentException("Unsupported method");
            }
        } catch (FeedException | URISyntaxException | ParseException | IOException e) {
//            throw new RuntimeException(e);
        }
        return false;
    }
}
