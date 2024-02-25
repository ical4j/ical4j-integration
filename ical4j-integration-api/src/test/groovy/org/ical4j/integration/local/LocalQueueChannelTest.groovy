package org.ical4j.integration.local

import net.fortuna.ical4j.model.Calendar
import org.ical4j.integration.EgressChannel
import org.ical4j.integration.IngressChannel
import spock.lang.Specification

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Flow
import java.util.concurrent.locks.ReentrantLock

class LocalQueueChannelTest extends Specification {

    def 'test send and receive calendar on local queue'() {
        given: 'a local queue'
        EgressChannel<Calendar, Calendar> queue = new LocalQueueChannel<>(new LinkedList<Calendar>())

        and: 'a subscriber'
        Calendar sent
        def lock = new ReentrantLock()
        lock.lock()
        Flow.Subscriber subscriber = [
                onSubscribe: {s ->
                    s.request(1)},
                onNext: {c ->
                    sent = c; lock.unlock()}
        ] as Flow.Subscriber
        queue.subscribe(subscriber)

        when: 'a calendar is sent to the queue'
        queue.send {new Calendar()}
        queue.close()

        then: 'it was sent successfully'
        lock.tryLock()
        sent != null
    }

    def 'test receive calendar on empty local queue'() {
        given: 'a local queue'
        IngressChannel<Calendar> queue = new LocalQueueChannel<>(new LinkedList<Calendar>())

        and: 'a subscriber'
        Calendar received
        queue.subscribe({ c -> received = c } as Flow.Subscriber)

        when: 'a receive is attempted from the queue'
        boolean success = queue.poll(0)

        then: 'nothing was received'
        !success && received == null
    }

    def 'test receive calendar on blocking local queue'() {
        given: 'a local queue'
        IngressChannel<Calendar> queue = new LocalQueueChannel<>(new ArrayBlockingQueue<Calendar>(1))

        and: 'a subscriber'
        Calendar received
        queue.subscribe({ c -> received = c } as Flow.Subscriber)

        when: 'a receive is attempted from the queue with a 5 second timeout'
        boolean success = queue.poll(5, true)

        then: 'nothing was received'
        !success && received == null
    }
}
