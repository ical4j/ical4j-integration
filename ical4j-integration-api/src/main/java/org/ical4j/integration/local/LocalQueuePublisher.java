package org.ical4j.integration.local;

import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Publish data received via the specified queue.
 *
 * @param <T> the data type
 */
public class LocalQueuePublisher<T extends Serializable> extends SubmissionPublisher<T> {

    private final Queue<T> queue;

    public LocalQueuePublisher(Queue<T> queue) {
        this.queue = queue;

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                if (queue instanceof BlockingQueue) {
                    submit(((BlockingQueue<T>) queue).poll(1000, TimeUnit.SECONDS));
                } else {
                    T next = this.queue.poll();
                    if (next != null) {
                        submit(next);
                    }
                }
            } catch (InterruptedException | NullPointerException e) {
                LoggerFactory.getLogger(LocalQueuePublisher.class).info("No data", e);
            }
        });
    }
}
