package org.ical4j.integration;

import java.util.Collections;
import java.util.Map;

/**
 * See: <a href="https://www.enterpriseintegrationpatterns.com/patterns/messaging/Message.html">Message</a>
 * @param <T> the supported message payload type
 */
public class Message<T> {

    private final Map<String, String> header;

    private final T body;

    public Message(Map<String, String> header, T body) {
        this.header = header;
        this.body = body;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public T getBody() {
        return body;
    }

    public static <T> Message<T> from(T body) {
        return new Message<>(Collections.emptyMap(), body);
    }
}
