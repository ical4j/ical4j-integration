package org.ical4j.integration.local

import net.fortuna.ical4j.model.Calendar
import spock.lang.Specification

import java.util.concurrent.Flow
import java.util.concurrent.SubmissionPublisher

class LocalQueuePublisherTest extends Specification {

    def 'test publish on local queue'() {
        given: 'a local queue'
        SubmissionPublisher<Calendar> queue = new LocalQueuePublisher<>(new LinkedList<Calendar>())

        and: 'a queue subscriber'
        Calendar received
        def subscriber = new Flow.Subscriber<Calendar>() {
            @Override
            void onSubscribe(Flow.Subscription subscription) {
                subscription.request(1)
            }

            @Override
            void onNext(Calendar item) {
                received = item
            }

            @Override
            void onError(Throwable throwable) {}

            @Override
            void onComplete() {}
        }
        queue.subscribe(subscriber)

        when: 'a calendar is sent to the queue'
        queue.submit(new Calendar())

        then: 'calendar is received by subscriber'
        received != null
    }
}
