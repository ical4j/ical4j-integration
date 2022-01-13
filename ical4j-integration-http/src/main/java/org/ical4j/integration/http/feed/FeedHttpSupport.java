package org.ical4j.integration.http.feed;

import com.rometools.rome.io.FeedException;
import net.fortuna.ical4j.model.Calendar;
import org.ical4j.integration.http.HttpSupport;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;

public class FeedHttpSupport implements HttpSupport {

    @Override
    public Calendar get(String path) throws IOException, FeedException, URISyntaxException, ParseException {
        return new FeedParser().parse(URI.create(path).toURL());
    }

    @Override
    public Calendar post(String path, String query) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void post(String path, Calendar calendar) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(String path, Calendar calendar) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void patch(String path, Calendar calendar) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(String path) {
        throw new UnsupportedOperationException();
    }
}
