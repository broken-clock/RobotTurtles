// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import java.util.Set;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import java.net.URI;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

public interface AsyncRestOperations
{
    RestOperations getRestOperations();
    
     <T> ListenableFuture<ResponseEntity<T>> getForEntity(final String p0, final Class<T> p1, final Object... p2) throws RestClientException;
    
     <T> ListenableFuture<ResponseEntity<T>> getForEntity(final String p0, final Class<T> p1, final Map<String, ?> p2) throws RestClientException;
    
     <T> ListenableFuture<ResponseEntity<T>> getForEntity(final URI p0, final Class<T> p1) throws RestClientException;
    
    ListenableFuture<HttpHeaders> headForHeaders(final String p0, final Object... p1) throws RestClientException;
    
    ListenableFuture<HttpHeaders> headForHeaders(final String p0, final Map<String, ?> p1) throws RestClientException;
    
    ListenableFuture<HttpHeaders> headForHeaders(final URI p0) throws RestClientException;
    
    ListenableFuture<URI> postForLocation(final String p0, final HttpEntity<?> p1, final Object... p2) throws RestClientException;
    
    ListenableFuture<URI> postForLocation(final String p0, final HttpEntity<?> p1, final Map<String, ?> p2) throws RestClientException;
    
    ListenableFuture<URI> postForLocation(final URI p0, final HttpEntity<?> p1) throws RestClientException;
    
     <T> ListenableFuture<ResponseEntity<T>> postForEntity(final String p0, final HttpEntity<?> p1, final Class<T> p2, final Object... p3) throws RestClientException;
    
     <T> ListenableFuture<ResponseEntity<T>> postForEntity(final String p0, final HttpEntity<?> p1, final Class<T> p2, final Map<String, ?> p3) throws RestClientException;
    
     <T> ListenableFuture<ResponseEntity<T>> postForEntity(final URI p0, final HttpEntity<?> p1, final Class<T> p2) throws RestClientException;
    
    ListenableFuture<?> put(final String p0, final HttpEntity<?> p1, final Object... p2) throws RestClientException;
    
    ListenableFuture<?> put(final String p0, final HttpEntity<?> p1, final Map<String, ?> p2) throws RestClientException;
    
    ListenableFuture<?> put(final URI p0, final HttpEntity<?> p1) throws RestClientException;
    
    ListenableFuture<?> delete(final String p0, final Object... p1) throws RestClientException;
    
    ListenableFuture<?> delete(final String p0, final Map<String, ?> p1) throws RestClientException;
    
    ListenableFuture<?> delete(final URI p0) throws RestClientException;
    
    ListenableFuture<Set<HttpMethod>> optionsForAllow(final String p0, final Object... p1) throws RestClientException;
    
    ListenableFuture<Set<HttpMethod>> optionsForAllow(final String p0, final Map<String, ?> p1) throws RestClientException;
    
    ListenableFuture<Set<HttpMethod>> optionsForAllow(final URI p0) throws RestClientException;
    
     <T> ListenableFuture<ResponseEntity<T>> exchange(final String p0, final HttpMethod p1, final HttpEntity<?> p2, final Class<T> p3, final Object... p4) throws RestClientException;
    
     <T> ListenableFuture<ResponseEntity<T>> exchange(final String p0, final HttpMethod p1, final HttpEntity<?> p2, final Class<T> p3, final Map<String, ?> p4) throws RestClientException;
    
     <T> ListenableFuture<ResponseEntity<T>> exchange(final URI p0, final HttpMethod p1, final HttpEntity<?> p2, final Class<T> p3) throws RestClientException;
    
     <T> ListenableFuture<ResponseEntity<T>> exchange(final String p0, final HttpMethod p1, final HttpEntity<?> p2, final ParameterizedTypeReference<T> p3, final Object... p4) throws RestClientException;
    
     <T> ListenableFuture<ResponseEntity<T>> exchange(final String p0, final HttpMethod p1, final HttpEntity<?> p2, final ParameterizedTypeReference<T> p3, final Map<String, ?> p4) throws RestClientException;
    
     <T> ListenableFuture<ResponseEntity<T>> exchange(final URI p0, final HttpMethod p1, final HttpEntity<?> p2, final ParameterizedTypeReference<T> p3) throws RestClientException;
    
     <T> ListenableFuture<T> execute(final String p0, final HttpMethod p1, final AsyncRequestCallback p2, final ResponseExtractor<T> p3, final Object... p4) throws RestClientException;
    
     <T> ListenableFuture<T> execute(final String p0, final HttpMethod p1, final AsyncRequestCallback p2, final ResponseExtractor<T> p3, final Map<String, ?> p4) throws RestClientException;
    
     <T> ListenableFuture<T> execute(final URI p0, final HttpMethod p1, final AsyncRequestCallback p2, final ResponseExtractor<T> p3) throws RestClientException;
}
