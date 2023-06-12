package org.ical4j.integration.http;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;

public class HttpRequestBuilder {

    public HttpRequest build() {
        return new HttpGet();
    }
}
