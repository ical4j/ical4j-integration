package org.ical4j.integration.mail

import jakarta.mail.Message
import jakarta.mail.Multipart

class JakartaMailSMTPChannelTest extends AbstractGreenMailSpec {

    def 'derives recipients and delivers a calendar via SMTP'() {
        given: 'a channel bound to the test session'
        def channel = new JakartaMailSMTPChannel(mailSession())
        def calendar = requestCalendar('uid-1', SENDER, [RECIPIENT])

        when: 'a REQUEST calendar is sent'
        boolean sent = channel.send { calendar }

        then: 'it is delivered with addressing derived from organizer/attendees'
        sent
        greenMail.waitForIncomingEmail(5000, 1)

        def messages = greenMail.receivedMessages
        messages.length == 1
        messages[0].getFrom()[0].toString() == SENDER
        messages[0].getRecipients(Message.RecipientType.TO)[0].toString() == RECIPIENT

        and: 'the message carries a text/calendar part'
        messages[0].content instanceof Multipart
        hasCalendarPart(messages[0].content as Multipart)
    }

    def 'reports failure when no message is produced'() {
        given: 'a builder that produces nothing'
        def channel = new JakartaMailSMTPChannel(mailSession(), { c -> Optional.empty() })

        expect:
        !channel.send { requestCalendar('uid-x', SENDER, [RECIPIENT]) }
    }

    private static boolean hasCalendarPart(Multipart multipart) {
        for (int i = 0; i < multipart.count; i++) {
            if (multipart.getBodyPart(i).isMimeType('text/calendar')) {
                return true
            }
        }
        return false
    }
}
