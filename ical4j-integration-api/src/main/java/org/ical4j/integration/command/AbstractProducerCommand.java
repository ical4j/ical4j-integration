package org.ical4j.integration.command;

import org.ical4j.integration.MessageProducer;

import java.util.function.Consumer;

/**
 * Subclasses provide functionality that requires data store connectivity.
 *
 * @param <T> the supported collection type for a configured data store
 * @param <R> the command result consumer
 */
public abstract class AbstractProducerCommand<T, R> extends AbstractCommand<R> {

    private final MessageProducer<T> endpoint;

    public AbstractProducerCommand() {
        this.endpoint = null;
    }

    public AbstractProducerCommand(MessageProducer<T> endpoint) {
        this.endpoint = endpoint;
    }

    public AbstractProducerCommand(Consumer<R> consumer) {
        super(consumer);
        this.endpoint = null;
    }

    public AbstractProducerCommand(Consumer<R> consumer, MessageProducer<T> endpoint) {
        super(consumer);
        this.endpoint = endpoint;
    }

    public MessageProducer<T> getEndpoint() {
        return endpoint;
    }
}
