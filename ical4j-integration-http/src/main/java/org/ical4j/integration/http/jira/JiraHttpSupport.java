package org.ical4j.integration.http.jira;

import net.fortuna.ical4j.model.Calendar;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.ical4j.integration.http.AbstractHttpClientSupport;

public class JiraHttpSupport extends AbstractHttpClientSupport {

    public JiraHttpSupport(HttpClient httpClient, HttpHost httpHost) {
        super(httpClient, httpHost);
    }

    @Override
    public Calendar get(String path) {
        return null;
    }

    @Override
    public Calendar post(String path, String query) {
        return null;
    }

    @Override
    public void post(String path, Calendar calendar) {

    }

    @Override
    public void put(String path, Calendar calendar) {

    }

    @Override
    public void patch(String path, Calendar calendar) {

    }

    @Override
    public void delete(String path) {

    }
}
