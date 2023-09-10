package org.ical4j.integration.local;

import org.ical4j.integration.ChannelPublisher;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Publish data received via the specified queue.
 *
 * @param <T> the data type
 */
public class LocalQueuePublisher<T> extends ChannelPublisher<T> {

    private final Queue<T> queue;

    public LocalQueuePublisher(Queue<T> queue) {
        this.queue = queue;
        Executor executor = Executors.newScheduledThreadPool(1);
        executor.execute(() -> submit(this.queue.poll()));
    }
}
