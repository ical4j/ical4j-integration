package org.ical4j.integration.http;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

import java.io.IOException;

public abstract class AbstractHttpClientSupport implements HttpSupport {

    private final HttpClient httpClient;

    private final HttpHost httpHost;

    public AbstractHttpClientSupport(HttpClient httpClient, HttpHost httpHost) {
        this.httpClient = httpClient;
        this.httpHost = httpHost;
    }

    protected HttpResponse execute(HttpRequest request) throws IOException {
        return httpClient.execute(httpHost, request);
    }
}
