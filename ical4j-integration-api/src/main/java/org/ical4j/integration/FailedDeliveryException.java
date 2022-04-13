package org.ical4j.integration;

public class FailedDeliveryException extends Exception {

    public FailedDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedDeliveryException(Throwable cause) {
        super(cause);
    }
}
