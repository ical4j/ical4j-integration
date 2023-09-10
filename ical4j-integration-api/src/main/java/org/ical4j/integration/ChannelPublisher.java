package org.ical4j.integration;

import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.BiConsumer;

/**
 * Extends {@link SubmissionPublisher} to support publishing data streams from supported channels.
 *
 * @param <T> the type of data stream
 */
public class ChannelPublisher<T> extends SubmissionPublisher<T> {

    public ChannelPublisher() {
    }

    public ChannelPublisher(Executor executor, int maxBufferCapacity) {
        super(executor, maxBufferCapacity);
    }

    public ChannelPublisher(Executor executor, int maxBufferCapacity, BiConsumer<? super Flow.Subscriber<? super T>, ? super Throwable> handler) {
        super(executor, maxBufferCapacity, handler);
    }
}
