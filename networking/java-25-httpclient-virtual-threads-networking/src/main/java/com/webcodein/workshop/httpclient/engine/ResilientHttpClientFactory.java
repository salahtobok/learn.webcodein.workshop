package com.webcodein.workshop.httpclient.engine;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executors;

public class ResilientHttpClientFactory {

    public static HttpClient createVirtualThreadHttpClient() {
        return createVirtualThreadHttpClient(Duration.ofSeconds(5));
    }

    public static HttpClient createVirtualThreadHttpClient(Duration connectTimeout) {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(connectTimeout)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                .build();
    }
}
