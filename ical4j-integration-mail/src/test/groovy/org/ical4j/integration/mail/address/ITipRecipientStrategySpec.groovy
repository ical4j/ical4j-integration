package org.ical4j.integration.mail.address

import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.ParameterList
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.parameter.Cn
import net.fortuna.ical4j.model.parameter.CuType
import net.fortuna.ical4j.model.parameter.SentBy
import net.fortuna.ical4j.model.property.Attendee
import net.fortuna.ical4j.model.property.Method
import net.fortuna.ical4j.model.property.Organizer
import net.fortuna.ical4j.model.property.ProdId
import net.fortuna.ical4j.model.property.Uid
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion
import spock.lang.Specification

class ITipRecipientStrategySpec extends Specification {

    def strategy = new ITipRecipientStrategy()

    def 'REQUEST routes from organizer to all attendees'() {
        given:
        Calendar calendar = cal(Method.VALUE_REQUEST,
                organizer('boss@example.com'),
                [attendee('a@example.com'), attendee('b@example.com')])

        when:
        def result = strategy.apply(calendar)

        then:
        result.isPresent()
        result.get().from.get().address == 'boss@example.com'
        result.get().to*.address == ['a@example.com', 'b@example.com']
    }

    def 'REPLY routes from the attendee to the organizer'() {
        given:
        Calendar calendar = cal(Method.VALUE_REPLY,
                organizer('boss@example.com'),
                [attendee('a@example.com')])

        when:
        def result = strategy.apply(calendar)

        then:
        result.isPresent()
        result.get().from.get().address == 'a@example.com'
        result.get().to*.address == ['boss@example.com']
    }

    def 'DECLINECOUNTER routes from organizer to the attendee'() {
        given:
        Calendar calendar = cal(Method.VALUE_DECLINECOUNTER,
                organizer('boss@example.com'),
                [attendee('a@example.com')])

        when:
        def result = strategy.apply(calendar)

        then:
        result.isPresent()
        result.get().from.get().address == 'boss@example.com'
        result.get().to*.address == ['a@example.com']
    }

    def 'PUBLISH derives sender only, no recipients'() {
        given:
        Calendar calendar = cal(Method.VALUE_PUBLISH, organizer('boss@example.com'), [])

        when:
        def result = strategy.apply(calendar)

        then:
        result.isPresent()
        result.get().from.get().address == 'boss@example.com'
        result.get().to.isEmpty()
    }

    def 'SENT-BY overrides the organizer From'() {
        given:
        Calendar calendar = cal(Method.VALUE_REQUEST,
                organizer('boss@example.com', 'The Boss', 'assistant@example.com'),
                [attendee('a@example.com')])

        when:
        def result = strategy.apply(calendar)

        then:
        result.get().from.get().address == 'assistant@example.com'
    }

    def 'CN populates the personal name'() {
        given:
        Calendar calendar = cal(Method.VALUE_REQUEST,
                organizer('boss@example.com', 'The Boss'),
                [attendee('a@example.com')])

        when:
        def result = strategy.apply(calendar)

        then:
        result.get().from.get().personal == 'The Boss'
    }

    def 'RESOURCE and ROOM attendees are excluded'() {
        given:
        Calendar calendar = cal(Method.VALUE_REQUEST,
                organizer('boss@example.com'),
                [attendee('a@example.com'), attendee('room@example.com', CuType.ROOM),
                 attendee('projector@example.com', CuType.RESOURCE)])

        when:
        def result = strategy.apply(calendar)

        then:
        result.get().to*.address == ['a@example.com']
    }

    def 'non-mailto attendees are skipped'() {
        given:
        Calendar calendar = cal(Method.VALUE_REQUEST,
                organizer('boss@example.com'),
                [attendee('a@example.com'), new Attendee(URI.create('urn:uuid:1234'))])

        when:
        def result = strategy.apply(calendar)

        then:
        result.get().to*.address == ['a@example.com']
    }

    def 'REPLY with multiple attendees cannot be derived'() {
        given:
        Calendar calendar = cal(Method.VALUE_REPLY,
                organizer('boss@example.com'),
                [attendee('a@example.com'), attendee('b@example.com')])

        expect:
        strategy.apply(calendar).isEmpty()
    }

    def 'REQUEST without attendees cannot be derived'() {
        given:
        Calendar calendar = cal(Method.VALUE_REQUEST, organizer('boss@example.com'), [])

        expect:
        strategy.apply(calendar).isEmpty()
    }

    private static Calendar cal(String method, Organizer organizer, List<Attendee> attendees) {
        VEvent event = new VEvent()
        event = event.add(new Uid('1'))
        if (organizer != null) {
            event = event.add(organizer)
        }
        for (Attendee attendee : attendees) {
            event = event.add(attendee)
        }

        Calendar calendar = new Calendar()
        calendar = calendar.add(new ProdId('-//test//EN'))
        calendar = calendar.add(ImmutableVersion.VERSION_2_0)
        if (method != null) {
            calendar = calendar.add(new Method(method))
        }
        calendar = calendar.add(event)
        return calendar
    }

    private static Organizer organizer(String email, String cn = null, String sentBy = null) {
        List params = []
        if (cn != null) {
            params << new Cn(cn)
        }
        if (sentBy != null) {
            params << new SentBy('mailto:' + sentBy)
        }
        return new Organizer(new ParameterList(params), URI.create('mailto:' + email))
    }

    private static Attendee attendee(String email, CuType cuType = null) {
        List params = []
        if (cuType != null) {
            params << cuType
        }
        return new Attendee(new ParameterList(params), URI.create('mailto:' + email))
    }
}
