package org.ical4j.integration.http;

import com.rometools.rome.io.FeedException;
import net.fortuna.ical4j.model.Calendar;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

public interface HttpSupport {

    Calendar get(String path) throws IOException, FeedException, URISyntaxException, ParseException;

    Calendar post(String path, String query);

    void post(String path, Calendar calendar);

    void put(String path, Calendar calendar);

    void patch(String path, Calendar calendar);

    void delete(String path);
}
