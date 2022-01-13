package org.ical4j.integration;

import net.fortuna.ical4j.model.Calendar;

/**
 * Event listener for consumption of REFRESH distribution method for iCalendar objects.
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc5546#section-1.4">iTIP - Methods</a>
 */
public interface CalendarRefreshListener {

    void onRefresh(Calendar calendar);
}
