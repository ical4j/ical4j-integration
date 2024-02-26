package org.ical4j.integration.flow;

import net.fortuna.ical4j.model.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Function;

public class CalendarProcessor<T> extends SubmissionPublisher<Calendar>
        implements Flow.Processor<T, Calendar> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarProcessor.class);

    private final Function<T, Optional<Calendar>> function;

    private Flow.Subscription subscription;

    public CalendarProcessor(Function<T, Optional<Calendar>> function) {
        this.function = function;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(T item) {
        function.apply(item).ifPresent(this::submit);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        LOGGER.error("Unexpected error", throwable);
    }

    @Override
    public void onComplete() {
        close();
    }
}
