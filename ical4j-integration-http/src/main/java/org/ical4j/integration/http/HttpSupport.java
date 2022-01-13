package org.ical4j.integration.http;

import net.fortuna.ical4j.model.Calendar;
import org.apache.http.client.ResponseHandler;

public interface HttpSupport {

    Calendar get(String path, ResponseHandler<Calendar> responseHandler);

    Calendar post(String path, ResponseHandler<Calendar> responseHandler);

    void post(String path, Calendar calendar);

    void put(String path, Calendar calendar);

    void patch(String path, Calendar calendar);

    void delete(String path);
}
