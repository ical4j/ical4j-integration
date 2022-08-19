package org.ical4j.integration.mail;

import jakarta.mail.Session;
import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.command.SendCalendarCommand;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * HTTP API for calendar mail delivery.
 */
@Component(
        service = {HttpServlet.class, Servlet.class},
        property = {"service.description=iCalendar Mail Servlet"}
)
@Designate(ocd = ICalendarMailServletConfiguration.class, factory = true)
public class ICalendarMailServlet extends HttpServlet {

    private CalendarMailTransport transport;

    @Override
    public void init() throws ServletException {
        super.init();
        transport = new CalendarMailTransport(Session.getDefaultInstance(null), null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        new SendCalendarCommand(transport).withCalendar(new Calendar()).run();
    }
}
