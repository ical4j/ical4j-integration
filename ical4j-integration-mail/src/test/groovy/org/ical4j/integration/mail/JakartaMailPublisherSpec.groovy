package org.ical4j.integration.mail

import net.fortuna.ical4j.model.Calendar
import org.ical4j.integration.mail.processor.CalendarAttachmentProcessor

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Flow
import java.util.concurrent.TimeUnit

class JakartaMailPublisherSpec extends AbstractGreenMailSpec {

    def 'emits a calendar to a subscriber when a message arrives'() {
        given: 'a subscribed publisher monitoring the inbox'
        def session = mailSession()
        def publisher = new JakartaMailPublisher(session, new CalendarAttachmentProcessor(), 'INBOX')
        def latch = new CountDownLatch(1)
        def received = new CopyOnWriteArrayList<Calendar>()
        publisher.subscribe(new Flow.Subscriber<Calendar>() {
            void onSubscribe(Flow.Subscription subscription) { subscription.request(Long.MAX_VALUE) }
            void onNext(Calendar calendar) { received << calendar; latch.countDown() }
            void onError(Throwable throwable) { }
            void onComplete() { }
        })

        when: 'a calendar arrives after subscription'
        new JakartaMailSMTPChannel(session).send { requestCalendar('pub-1', SENDER, [RECIPIENT]) }

        then: 'the subscriber receives the parsed calendar'
        latch.await(15, TimeUnit.SECONDS)
        received.size() >= 1
        received[0].getComponent('VEVENT').get().getRequiredProperty('UID').value == 'pub-1'

        cleanup:
        publisher.close()
    }
}
