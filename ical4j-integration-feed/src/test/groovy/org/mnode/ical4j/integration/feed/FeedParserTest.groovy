package org.mnode.ical4j.integration.feed

import net.fortuna.ical4j.model.Calendar
import spock.lang.Specification

class FeedParserTest extends Specification {

    def 'Test basic calendar generation'() {
        given: 'a feed url'
        URL url = new URL(feed)

        and: 'a feed parser'
        FeedParser parser = []

        when: 'the feed is parsed'
        Calendar calendar = parser.parse(url)

        then: 'the expected calendar is produced'
        calendar as String  == 'BEGIN:VCALENDAR\r\nNAME:Slashdot\r\nEND:VCALENDAR\r\n'

        where:
        feed << [
                "http://rss.slashdot.org/Slashdot/slashdot",
                'https://www.abc.net.au/news/feed/51120/rss.xml'
        ]
    }
}
