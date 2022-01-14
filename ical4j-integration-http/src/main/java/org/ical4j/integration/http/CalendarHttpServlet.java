package org.ical4j.integration.http;

import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.CalendarListenerSupport;
import org.ical4j.integration.CalendarPublishListener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CalendarHttpServlet extends HttpServlet implements CalendarListenerSupport {

    private List<CalendarPublishListener> publishListenerList = new ArrayList<>();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        fireCalendarEvent(parseCalendar(req));
    }

    private Calendar parseCalendar(HttpServletRequest request) {
        return null;
    }

    @Override
    public List<CalendarPublishListener> getCalendarPublishListeners() {
        return publishListenerList;
    }
}
