package org.mnode.ical4j.integration.feed

import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedOutput
import net.fortuna.ical4j.model.ContentBuilder
import net.fortuna.ical4j.model.property.Method
import net.fortuna.ical4j.model.property.ProdId
import net.fortuna.ical4j.model.property.Version
import spock.lang.Specification

class FeedEmitterTest extends Specification {

    ContentBuilder builder = []
    
    ProdId prodId = ['-//Ben Fortuna//iCal4j 2.0//EN']

    def 'test basic feed generation'() {
        given: 'a calendar'
        def calendar = builder.calendar {
            prodid(prodId)
            version(Version.VERSION_2_0)
            method(Method.PUBLISH)
            name('Test Calendar')
            vevent {
                organizer 'mailto:org@example.com'
                summary 'Spring Equinox'
                dtstamp()
                dtstart '20090810', parameters: parameters { value 'DATE' }
                dtend '20090811', parameters: parameters { value 'DATE' }
                action 'DISPLAY'
                attach'http://example.com/attachment', parameters: parameters { value 'URI' }
                attendee 'mailto:org@example.com'
            }
        }

        and: 'a feed emitter'
        FeedEmitter emitter = []

        when: 'feed is generated'
        SyndFeed feed = emitter.emit(calendar)

        then: 'result is as expected'
        SyndFeedOutput out = []
        out.outputString(feed) == ''
    }
}
