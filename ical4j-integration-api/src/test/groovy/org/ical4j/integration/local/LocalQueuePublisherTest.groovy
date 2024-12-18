package org.ical4j.integration.local

import net.fortuna.ical4j.model.Calendar
import spock.lang.Specification

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Flow
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.SubmissionPublisher

class LocalQueuePublisherTest extends Specification {

    def 'test publish on local queue'() {
        given: 'a local queue'
        def queue = new LinkedBlockingQueue<Calendar>()
        SubmissionPublisher<Calendar> publisher = new LocalQueuePublisher<>(queue)

        and: 'a queue subscriber'
        Calendar received
        CountDownLatch latch = [1]

        def subscriber = new Flow.Subscriber<Calendar>() {
            @Override
            void onSubscribe(Flow.Subscription subscription) {
                subscription.request(1)
            }

            @Override
            void onNext(Calendar item) {
                received = item
                latch.countDown()
            }

            @Override
            void onError(Throwable throwable) {}

            @Override
            void onComplete() {}
        }
        publisher.subscribe(subscriber)

        when: 'a calendar is sent to the queue'
        queue.add(new Calendar())

        then: 'calendar is received by subscriber'
        latch.await()
        received != null
    }
}
