package com.reliaquest.api.httpclient;

import org.springframework.http.ResponseEntity;

public interface IHttpClient {
    <T> ResponseEntity<T> get(String url, Class<T> responseType);

    <T> ResponseEntity<T> post(String url, Object request, Class<T> responseType);

    <T> ResponseEntity<T> delete(String url, Object request, Class<T> responseType);
}
