package org.ical4j.integration.mail


import jakarta.mail.internet.MimeMessage
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.property.immutable.ImmutableMethod
import org.ical4j.integration.EgressChannel
import spock.lang.Specification

class JakartaMailSMTPChannelTest extends Specification {

    def 'test publish with different email providers'() {
        given: 'a calendar instance'
        Calendar calendar = new Calendar().withDefaults().withProdId(JakartaMailSMTPChannel.name)
                .withProperty(ImmutableMethod.PUBLISH).fluentTarget

        when: 'the calendar is published'
        EgressChannel<Calendar> channel = new JakartaMailSMTPChannel((c) -> {
            return Optional.of(new MimeMessage(null))
        })
        channel.send {calendar}

        then: 'no errors are raised'

    }
}
