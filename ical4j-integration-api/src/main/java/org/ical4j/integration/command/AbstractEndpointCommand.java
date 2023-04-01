package org.ical4j.integration.command;

import org.ical4j.integration.MessageEndpoint;

import java.util.function.Consumer;

/**
 * Subclasses provide functionality that requires data store connectivity.
 *
 * @param <T> the supported collection type for a configured data store
 * @param <R> the command result consumer
 */
public abstract class AbstractEndpointCommand<T, R> extends AbstractCommand<R> {

    private final MessageEndpoint<T> endpoint;

    public AbstractEndpointCommand() {
        this.endpoint = null;
    }

    public AbstractEndpointCommand(MessageEndpoint<T> endpoint) {
        this.endpoint = endpoint;
    }

    public AbstractEndpointCommand(Consumer<R> consumer) {
        super(consumer);
        this.endpoint = null;
    }

    public AbstractEndpointCommand(Consumer<R> consumer, MessageEndpoint<T> endpoint) {
        super(consumer);
        this.endpoint = endpoint;
    }

    public MessageEndpoint<T> getEndpoint() {
        return endpoint;
    }
}
