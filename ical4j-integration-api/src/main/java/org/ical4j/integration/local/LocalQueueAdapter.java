package org.ical4j.integration.local;

import org.ical4j.integration.ChannelAdapter;
import org.ical4j.integration.ChannelConsumer;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LocalQueueAdapter<T> implements ChannelAdapter<T>, ChannelConsumer<T> {

    private final Queue<T> queue;

    public LocalQueueAdapter(Queue<T> queue) {
        this.queue = queue;
    }

    @Override
    public boolean publish(Supplier<T> supplier) {
        return queue.add(supplier.get());
    }

    @Override
    public boolean consume(Consumer<T> consumer, long timeout, boolean autoExpunge) {
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
            LoggerFactory.getLogger(LocalQueueAdapter.class).info("No data", e);
        }
        return false;
    }
}
