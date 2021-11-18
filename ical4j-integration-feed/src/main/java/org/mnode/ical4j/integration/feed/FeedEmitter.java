package org.mnode.ical4j.integration.feed;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.property.Name;

/**
 * Output iCalendar objects as Atom feed XML.
 */
public class FeedEmitter {

    public SyndFeed emit(Calendar calendar) {
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("atom_1.0");
        feed.setTitle(getTitle(calendar));
        return feed;
    }

    private String getTitle(Calendar calendar) {
        Name name = calendar.getProperty(Property.NAME);
        return name.getValue();
    }
}
