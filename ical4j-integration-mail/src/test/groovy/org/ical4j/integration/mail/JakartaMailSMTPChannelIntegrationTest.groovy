package org.ical4j.integration.mail

import jakarta.mail.Message
import jakarta.mail.internet.MimeMessage
import net.fortuna.ical4j.model.Calendar
import org.ical4j.integration.mail.builder.EventAttachmentBuilder

/**
 * Verifies that caller-supplied addressing overrides derivation, sent end-to-end through GreenMail.
 */
class JakartaMailSMTPChannelIntegrationTest extends AbstractGreenMailSpec {

    def 'caller-supplied recipients override derivation'() {
        given: 'a builder whose caller overrides the recipient after build'
        def session = mailSession()
        def builder = new EventAttachmentBuilder(session)
        def channel = new JakartaMailSMTPChannel(session, { Calendar c ->
            Optional<MimeMessage> message = builder.apply(c)
            message.ifPresent { it.setRecipients(Message.RecipientType.TO, RECIPIENT) }
            return message
        })

        and: 'a calendar whose derived recipient would be someone else'
        def calendar = requestCalendar('uid-2', SENDER, ['someoneelse@example.com'])

        when: 'the calendar is sent'
        boolean sent = channel.send { calendar }

        then: 'the caller-supplied recipient is used, From is still derived from the organizer'
        sent
        greenMail.waitForIncomingEmail(5000, 1)

        def messages = greenMail.receivedMessages
        messages.length == 1
        messages[0].getRecipients(Message.RecipientType.TO)[0].toString() == RECIPIENT
        messages[0].getFrom()[0].toString() == SENDER
    }
}
