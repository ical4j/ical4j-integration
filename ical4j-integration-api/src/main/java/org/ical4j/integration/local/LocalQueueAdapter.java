package org.ical4j.integration.local;

import org.ical4j.integration.ChannelAdapter;

import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LocalQueueAdapter<T> implements ChannelAdapter<T> {

    private final Queue<T> queue;

    public LocalQueueAdapter(Queue<T> queue) {
        this.queue = queue;
    }

    @Override
    public boolean send(Supplier<T> supplier) {
        return queue.add(supplier.get());
    }

    @Override
    public boolean receive(Consumer<T> consumer, long timeout, boolean autoExpunge) {
        T message;
        if (autoExpunge) {
            message = queue.poll();
        } else {
            message = queue.peek();
        }
        if (message != null) {
            consumer.accept(message);
            return true;
        }
        return false;
    }
}
