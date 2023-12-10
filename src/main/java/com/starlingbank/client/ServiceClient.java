package com.starlingbank.client;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Component
public class ServiceClient {

    public HttpResponse<String> get(String url,
                                           Map<String, String> queryParams,
                                           Map<String, String> pathParams,
                                           String... headers) throws IOException, InterruptedException {
        // Create an instance of HttpClient
        HttpClient httpClient = HttpClient.newHttpClient();

        // Specify the URL of the outbound service
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(buildUrl(url, queryParams, pathParams)))
                .GET()
                .headers(headers)
                .build();

            // Send the HTTP request and receive the response
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> put(String url,
                                           Map<String, String> queryParams,
                                           Map<String, String> pathParams,
                                           String jsonBody,
                                           String... headers) throws IOException, InterruptedException {
        // Create an instance of HttpClient
        HttpClient httpClient = HttpClient.newHttpClient();

        // Specify the URL of the outbound service
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(buildUrl(url, queryParams, pathParams)))
                .headers(headers)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        // Send the HTTP request and receive the response
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }



    private static String buildUrl(String baseUrl, Map<String, String> queryParams, Map<String, String> pathParams) {
        // Add path parameters to the base URL
        for (Map.Entry<String, String> entry : pathParams.entrySet()) {
            baseUrl = baseUrl.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        // Build the final URL with query parameters
        StringBuilder urlBuilder = new StringBuilder(baseUrl);

        // Add query parameters
        if (!queryParams.isEmpty()) {
            urlBuilder.append("?");
            queryParams.forEach((key, value) -> urlBuilder.append(key).append("=").append(value).append("&"));
            // Remove the last "&"
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }

        return urlBuilder.toString();
    }
}
