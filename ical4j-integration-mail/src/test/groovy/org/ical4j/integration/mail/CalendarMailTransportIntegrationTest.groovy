package org.ical4j.integration.mail

import jakarta.mail.Session
import net.fortuna.ical4j.model.ContentBuilder
import org.testcontainers.containers.GenericContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

@Testcontainers
class CalendarMailTransportIntegrationTest extends Specification {

    @Shared
    GenericContainer mailhog = new GenericContainer('mailhog/mailhog')
            .withExposedPorts(1025, 8025)

    Session session

    def setup() {
        Properties sessionProps = []
        sessionProps << [
            'mail.smtp.host': mailhog.getContainerIpAddress(),
            'mail.smtp.port': mailhog.getMappedPort(1025)
        ]
        session = Session.getDefaultInstance(sessionProps)
        session.debug = true
    }

    def 'assert send calendar functionality works'() {
        given: 'a calendar mail transport instance'
        CalendarMailTransport transport = [session, null]
        transport.withMessageTemplate(new MessageTemplate()
                .withFromAddress("sender@example.com")
                .withToAddress("recipient@example.com")
                .withSubject("Test calendar mail transport send")
                .withTextBody("This is an integration test"))

        when: 'a calendar is submitted'
        transport.send(new ContentBuilder().calendar() {
            prodid '-//Ben Fortuna//iCal4j 3.1//EN'
            version '2.0'
            method 'PUBLISH'
            uid '123'
            vevent {
                uid '2'
                summary 'Test Event 2'
                organizer 'johnd@example.com', parameters: parameters { cn 'John Doe' }
                dtstamp()
                dtstart '20100810', parameters: parameters { value 'DATE' }
                description 'Test Description 2', parameters: parameters { xparameter name: 'x-format', value: 'text/plain' }
                xproperty 'X-test', value: 'test-value'
            }
        })

        then: 'it is successfully sent'
        true
    }
}
