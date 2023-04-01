package org.ical4j.integration.command;

import org.ical4j.integration.MessageConsumer;

import java.util.function.Consumer;

/**
 * Subclasses provide functionality that requires data store connectivity.
 *
 * @param <T> the supported collection type for a configured data store
 * @param <R> the command result consumer
 */
public abstract class AbstractConsumerCommand<T, R> extends AbstractCommand<R> {

    private final MessageConsumer<T> endpoint;

    public AbstractConsumerCommand() {
        this.endpoint = null;
    }

    public AbstractConsumerCommand(MessageConsumer<T> endpoint) {
        this.endpoint = endpoint;
    }

    public AbstractConsumerCommand(Consumer<R> consumer) {
        super(consumer);
        this.endpoint = null;
    }

    public AbstractConsumerCommand(Consumer<R> consumer, MessageConsumer<T> endpoint) {
        super(consumer);
        this.endpoint = endpoint;
    }

    public MessageConsumer<T> getEndpoint() {
        return endpoint;
    }
}
