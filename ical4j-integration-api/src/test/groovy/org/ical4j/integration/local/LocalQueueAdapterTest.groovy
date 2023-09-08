package org.ical4j.integration.local

import net.fortuna.ical4j.model.Calendar
import org.ical4j.integration.ChannelAdapter
import spock.lang.Specification

import java.util.concurrent.ArrayBlockingQueue

class LocalQueueAdapterTest extends Specification {

    def 'test send and receive calendar on local queue'() {
        given: 'a local queue'
        ChannelAdapter<Calendar> queue = new LocalQueueAdapter<>(new LinkedList<Calendar>())

        when: 'a calendar is sent to the queue'
        queue.publish {new Calendar()}
        Calendar received
        boolean success = queue.consume({ c -> received = c }, 0)

        then: 'it was received successfully'
        success && received != null
    }

    def 'test receive calendar on empty local queue'() {
        given: 'a local queue'
        ChannelAdapter<Calendar> queue = new LocalQueueAdapter<>(new LinkedList<Calendar>())

        when: 'a receive is attempted from the queue'
        Calendar received
        boolean success = queue.consume({ c -> received = c }, 0)

        then: 'nothing was received'
        !success && received == null
    }

    def 'test receive calendar on blocking local queue'() {
        given: 'a local queue'
        ChannelAdapter<Calendar> queue = new LocalQueueAdapter<>(new ArrayBlockingQueue<Calendar>(1))

        when: 'a receive is attempted from the queue with a 5 second timeout'
        Calendar received
        boolean success = queue.consume({ c -> received = c }, 5, true)

        then: 'nothing was received'
        !success && received == null
    }
}
