package org.ical4j.integration.http;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.ical4j.integration.ChannelAdapter;
import org.ical4j.integration.ChannelConsumer;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ApacheHttpClientAdapter<T> implements ChannelAdapter<T>, ChannelConsumer<T> {

    private final HttpClient httpClient;

    private final HttpHost httpHost;

    private final HttpRequestBuilder requestBuilder;

    private final ResponseHandler<T> responseHandler;

    public ApacheHttpClientAdapter(HttpClient httpClient, HttpHost httpHost, HttpRequestBuilder requestBuilder,
                                   ResponseHandler<T> responseHandler) {
        this.httpClient = httpClient;
        this.httpHost = httpHost;
        this.requestBuilder = requestBuilder;
        this.responseHandler = responseHandler;
    }

    @Override
    public boolean consume(Consumer<T> consumer, long timeout, boolean autoExpunge) {
        try {
            consumer.accept(httpClient.execute(httpHost, requestBuilder.build(), responseHandler));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean publish(Supplier<T> supplier) {
        HttpRequest request = requestBuilder.build();
        try {
            httpClient.execute(httpHost, request, response -> response.getStatusLine().getStatusCode() >= 200);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
