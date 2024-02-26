package org.ical4j.integration.flow;


import net.fortuna.ical4j.vcard.VCard;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Function;

public class VCardProcessor<T> extends SubmissionPublisher<VCard> implements Flow.Processor<T, VCard> {

    private final Function<T, VCard> function;

    private Flow.Subscription subscription;

    public VCardProcessor(Function<T, VCard> function) {
        this.function = function;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(T item) {
        submit(function.apply(item));
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {
        close();
    }
}
