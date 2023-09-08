package org.ical4j.integration.local;

import org.ical4j.integration.AbstractChannelPublisher;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LocalQueuePublisher<T> extends AbstractChannelPublisher<T> {

    private final Queue<T> queue;

    public LocalQueuePublisher(Queue<T> queue) {
        this.queue = queue;
//        while (true) {
//            publish(queue.poll());
//        }
        Executor executor = Executors.newScheduledThreadPool(1);
        executor.execute(() -> publish(this.queue.poll()));
    }
}
