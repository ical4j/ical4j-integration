package org.ical4j.integration.mail

import net.fortuna.ical4j.model.Calendar
import org.ical4j.integration.mail.processor.CalendarAttachmentProcessor

class JakartaMailPollingChannelSpec extends AbstractGreenMailSpec {

    def 'round-trips a calendar from SMTP send to IMAP poll'() {
        given:
        def session = mailSession()
        def egress = new JakartaMailSMTPChannel(session)
        def ingress = new JakartaMailPollingChannel(session, new CalendarAttachmentProcessor())

        when: 'a calendar is sent and delivered'
        egress.send { requestCalendar('round-trip-1', SENDER, [RECIPIENT]) }
        greenMail.waitForIncomingEmail(5000, 1)

        and: 'the inbox is polled'
        List<Calendar> received = []
        boolean consumed = ingress.poll({ received << it }, 5)

        then:
        consumed
        received.size() == 1
        received[0].getComponent('VEVENT').get().getRequiredProperty('UID').value == 'round-trip-1'
    }

    def 'autoExpunge removes the consumed message'() {
        given:
        def session = mailSession()
        def egress = new JakartaMailSMTPChannel(session)
        def ingress = new JakartaMailPollingChannel(session, new CalendarAttachmentProcessor())

        when: 'a delivered message is polled with autoExpunge'
        egress.send { requestCalendar('expunge-1', SENDER, [RECIPIENT]) }
        greenMail.waitForIncomingEmail(5000, 1)
        ingress.poll({ }, 5, true)

        and: 'a second poll finds nothing'
        List<Calendar> received = []
        boolean consumed = ingress.poll({ received << it }, 2)

        then:
        !consumed
        received.isEmpty()
    }

    def 'expunge by uid removes the matching message'() {
        given:
        def session = mailSession()
        def egress = new JakartaMailSMTPChannel(session)
        def ingress = new JakartaMailPollingChannel(session, new CalendarAttachmentProcessor())

        when:
        egress.send { requestCalendar('expunge-uid', SENDER, [RECIPIENT]) }
        greenMail.waitForIncomingEmail(5000, 1)
        boolean removed = ingress.expunge('expunge-uid')

        and:
        List<Calendar> received = []
        ingress.poll({ received << it }, 2)

        then:
        removed
        received.isEmpty()
    }
}
