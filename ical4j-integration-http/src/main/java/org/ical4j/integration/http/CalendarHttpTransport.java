package org.ical4j.integration.http;

import net.fortuna.ical4j.model.Calendar;
import org.apache.http.client.ResponseHandler;
import org.ical4j.integration.CalendarConsumer;
import org.ical4j.integration.CalendarProducer;
import org.ical4j.integration.FailedDeliveryException;

/**
 * HTTP implementation of a calendar transport.
 */
public class CalendarHttpTransport implements CalendarProducer, CalendarConsumer {

    private final HttpSupport httpSupport;

    private final String method;

    private final String path;

    private final ResponseHandler<Calendar> responseHandler;

    public CalendarHttpTransport(HttpSupport httpSupport, String method, String path,
                                 ResponseHandler<Calendar> responseHandler) {
        this.httpSupport = httpSupport;
        this.method = method;
        this.path = path;
        this.responseHandler = responseHandler;
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
        switch (method) {
            case "GET":
                return httpSupport.get(path, responseHandler);
            case "POST":
                return httpSupport.post(path, responseHandler);
            default:
                throw new IllegalArgumentException("Unsupported method");
        }
    }
}
