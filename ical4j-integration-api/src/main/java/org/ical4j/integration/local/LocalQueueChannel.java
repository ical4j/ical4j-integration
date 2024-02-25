package org.ical4j.integration.local;

import org.ical4j.integration.EgressChannel;
import org.ical4j.integration.IngressChannel;
import org.ical4j.integration.flow.ChannelPublisher;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class LocalQueueChannel<T> extends ChannelPublisher<T> implements EgressChannel<T, T>, IngressChannel<T> {

    private final Queue<T> queue;

    public LocalQueueChannel(Queue<T> queue) {
        this.queue = queue;
    }

    @Override
    public boolean send(Supplier<T> supplier) {
        if (queue.add(supplier.get())) {
            submit(supplier.get());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean poll(long timeout, boolean autoExpunge) {
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
            submit(message);
            return true;
        } catch (InterruptedException | NullPointerException e) {
            LoggerFactory.getLogger(LocalQueueChannel.class).info("No data", e);
        }
        return false;
    }
}
