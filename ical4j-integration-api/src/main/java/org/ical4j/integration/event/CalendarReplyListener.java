package org.ical4j.integration.event;

import net.fortuna.ical4j.model.Calendar;

/**
 * Event listener for consumption of REPLY distribution method for iCalendar objects.
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc5546#section-1.4">iTIP - Methods</a>
 */
public interface CalendarReplyListener {

    void onReply(Calendar calendar);
}
