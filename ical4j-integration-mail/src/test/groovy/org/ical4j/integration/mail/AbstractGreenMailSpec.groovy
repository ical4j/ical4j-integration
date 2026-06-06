package org.ical4j.integration.mail

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetupTest
import jakarta.mail.Authenticator
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Attendee
import net.fortuna.ical4j.model.property.Method
import net.fortuna.ical4j.model.property.Organizer
import net.fortuna.ical4j.model.property.ProdId
import net.fortuna.ical4j.model.property.Summary
import net.fortuna.ical4j.model.property.Uid
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion
import spock.lang.Specification

/**
 * Base specification that starts an in-JVM GreenMail server (SMTP + IMAP, with IDLE support) and
 * provides a configured {@link Session} plus helpers for building test calendars.
 */
abstract class AbstractGreenMailSpec extends Specification {

    static final String SENDER = 'sender@example.com'
    static final String RECIPIENT = 'recipient@example.com'
    static final String LOGIN = 'recipient'
    static final String PASSWORD = 'password'

    GreenMail greenMail

    def setup() {
        greenMail = new GreenMail(ServerSetupTest.SMTP_IMAP)
        greenMail.start()
        greenMail.setUser(RECIPIENT, LOGIN, PASSWORD)
    }

    def cleanup() {
        greenMail?.stop()
    }

    Session mailSession() {
        Properties props = new Properties()
        props.setProperty('mail.smtp.host', 'localhost')
        props.setProperty('mail.smtp.port', String.valueOf(greenMail.smtp.port))
        props.setProperty('mail.transport.protocol', 'smtp')
        props.setProperty('mail.store.protocol', 'imap')
        props.setProperty('mail.imap.host', 'localhost')
        props.setProperty('mail.imap.port', String.valueOf(greenMail.imap.port))
        props.setProperty('mail.imap.user', LOGIN)
        return Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(LOGIN, PASSWORD)
            }
        })
    }

    /**
     * Build a minimal REQUEST calendar with the given organizer and attendees (bare email
     * addresses; the {@code mailto:} scheme is added automatically).
     */
    static Calendar requestCalendar(String uid, String organizerEmail, List<String> attendeeEmails) {
        VEvent event = new VEvent()
        event = event.add(new Uid(uid))
        event = event.add(new Summary('Test Event'))
        event = event.add(new Organizer(URI.create('mailto:' + organizerEmail)))
        for (String attendee : attendeeEmails) {
            event = event.add(new Attendee(URI.create('mailto:' + attendee)))
        }

        Calendar calendar = new Calendar()
        calendar = calendar.add(new ProdId('-//ical4j//integration test//EN'))
        calendar = calendar.add(ImmutableVersion.VERSION_2_0)
        calendar = calendar.add(new Method(Method.VALUE_REQUEST))
        calendar = calendar.add(event)
        return calendar
    }
}
