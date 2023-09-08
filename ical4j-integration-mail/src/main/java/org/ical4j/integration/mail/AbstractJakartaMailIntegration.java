package org.ical4j.integration.mail;

import jakarta.mail.Session;

public abstract class AbstractJakartaMailIntegration {

    private final Session session;

    public AbstractJakartaMailIntegration(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}
