package org.ical4j.integration.webhook;

import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.CalendarListenerSupport;
import org.ical4j.integration.ListenerList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CalendarHttpServlet extends HttpServlet implements CalendarListenerSupport {

    private final ListenerList<Object> listenerList = new ListenerList<>();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        fireCalendarEvent(parseCalendar(req));
    }

    private Calendar parseCalendar(HttpServletRequest request) {
        return null;
    }

    @Override
    public ListenerList<Object> getCalendarListeners() {
        return listenerList;
    }
}
