package org.ical4j.integration.local;

import org.ical4j.integration.EgressChannel;
import org.ical4j.integration.IngressChannel;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LocalQueueChannel<T> implements EgressChannel<T>, IngressChannel<T> {

    private final Queue<T> queue;

    public LocalQueueChannel(Queue<T> queue) {
        this.queue = queue;
    }

    @Override
    public boolean send(Supplier<T> supplier) {
        if (queue.add(supplier.get())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean poll(Consumer<T> consumer, long timeout) {
        return poll(consumer, timeout, false);
    }

    @Override
    public boolean poll(Consumer<T> consumer, long timeout, boolean autoExpunge) {
        T message;
        try {
            if (autoExpunge) {
                if (queue instanceof BlockingQueue) {
                    message = Objects.requireNonNull(((BlockingQueue<T>) queue).poll(timeout, TimeUnit.SECONDS));
                } else {
                    message = Objects.requireNonNull(queue.poll());
                }
            } else {
                message = Objects.requireNonNull(queue.peek());
            }
            consumer.accept(message);
            return true;
        } catch (InterruptedException | NullPointerException e) {
            LoggerFactory.getLogger(LocalQueueChannel.class).info("No data", e);
        }
        return false;
    }
}
