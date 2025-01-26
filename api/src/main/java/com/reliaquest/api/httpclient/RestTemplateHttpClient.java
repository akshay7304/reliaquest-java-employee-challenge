package com.reliaquest.api.httpclient;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateHttpClient implements IHttpClient {
    private final RestTemplate restTemplate;

    public RestTemplateHttpClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public <T> ResponseEntity<T> get(String url, Class<T> responseType) {
        return restTemplate.getForEntity(url, responseType);
    }

    @Override
    public <T> ResponseEntity<T> post(String url, Object request, Class<T> responseType) {
        return restTemplate.postForEntity(url, request, responseType);
    }

    @Override
    public <T> ResponseEntity<T> delete(String url, Object request, Class<T> responseType) {
        HttpEntity<Object> entity = new HttpEntity<>(request);
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, responseType);
    }
}
