package org.ical4j.integration.local

import net.fortuna.ical4j.model.Calendar
import org.ical4j.integration.EgressChannel
import org.ical4j.integration.IngressChannel
import spock.lang.Specification

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.locks.ReentrantLock

class LocalQueueChannelTest extends Specification {

    def 'test send and receive calendar on local queue'() {
        given: 'a local queue'
        EgressChannel<Calendar> queue = new LocalQueueChannel<>(new LinkedList<Calendar>())

        and: 'a subscriber'
        Calendar sent
        def lock = new ReentrantLock()
        lock.lock()

        when: 'a calendar is sent to the queue'
        queue.send {new Calendar()}

        then: 'it was sent successfully'
        queue.poll({c -> sent = c; lock.unlock()}, 0)
        lock.tryLock()
        sent != null
    }

    def 'test receive calendar on empty local queue'() {
        given: 'a local queue'
        IngressChannel<Calendar> queue = new LocalQueueChannel<>(new LinkedList<Calendar>())

        and: 'a subscriber'
        Calendar received

        when: 'a receive is attempted from the queue'
        boolean success = queue.poll({ c -> received = c },0)

        then: 'nothing was received'
        !success && received == null
    }

    def 'test receive calendar on blocking local queue'() {
        given: 'a local queue'
        IngressChannel<Calendar> queue = new LocalQueueChannel<>(new ArrayBlockingQueue<Calendar>(1))

        and: 'a subscriber'
        Calendar received

        when: 'a receive is attempted from the queue with a 5 second timeout'
        boolean success = queue.poll({ c -> received = c },5, true)

        then: 'nothing was received'
        !success && received == null
    }
}
