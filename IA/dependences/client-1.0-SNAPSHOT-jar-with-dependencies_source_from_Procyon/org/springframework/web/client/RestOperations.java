// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import java.util.Map;

public interface RestOperations
{
     <T> T getForObject(final String p0, final Class<T> p1, final Object... p2) throws RestClientException;
    
     <T> T getForObject(final String p0, final Class<T> p1, final Map<String, ?> p2) throws RestClientException;
    
     <T> T getForObject(final URI p0, final Class<T> p1) throws RestClientException;
    
     <T> ResponseEntity<T> getForEntity(final String p0, final Class<T> p1, final Object... p2) throws RestClientException;
    
     <T> ResponseEntity<T> getForEntity(final String p0, final Class<T> p1, final Map<String, ?> p2) throws RestClientException;
    
     <T> ResponseEntity<T> getForEntity(final URI p0, final Class<T> p1) throws RestClientException;
    
    HttpHeaders headForHeaders(final String p0, final Object... p1) throws RestClientException;
    
    HttpHeaders headForHeaders(final String p0, final Map<String, ?> p1) throws RestClientException;
    
    HttpHeaders headForHeaders(final URI p0) throws RestClientException;
    
    URI postForLocation(final String p0, final Object p1, final Object... p2) throws RestClientException;
    
    URI postForLocation(final String p0, final Object p1, final Map<String, ?> p2) throws RestClientException;
    
    URI postForLocation(final URI p0, final Object p1) throws RestClientException;
    
     <T> T postForObject(final String p0, final Object p1, final Class<T> p2, final Object... p3) throws RestClientException;
    
     <T> T postForObject(final String p0, final Object p1, final Class<T> p2, final Map<String, ?> p3) throws RestClientException;
    
     <T> T postForObject(final URI p0, final Object p1, final Class<T> p2) throws RestClientException;
    
     <T> ResponseEntity<T> postForEntity(final String p0, final Object p1, final Class<T> p2, final Object... p3) throws RestClientException;
    
     <T> ResponseEntity<T> postForEntity(final String p0, final Object p1, final Class<T> p2, final Map<String, ?> p3) throws RestClientException;
    
     <T> ResponseEntity<T> postForEntity(final URI p0, final Object p1, final Class<T> p2) throws RestClientException;
    
    void put(final String p0, final Object p1, final Object... p2) throws RestClientException;
    
    void put(final String p0, final Object p1, final Map<String, ?> p2) throws RestClientException;
    
    void put(final URI p0, final Object p1) throws RestClientException;
    
    void delete(final String p0, final Object... p1) throws RestClientException;
    
    void delete(final String p0, final Map<String, ?> p1) throws RestClientException;
    
    void delete(final URI p0) throws RestClientException;
    
    Set<HttpMethod> optionsForAllow(final String p0, final Object... p1) throws RestClientException;
    
    Set<HttpMethod> optionsForAllow(final String p0, final Map<String, ?> p1) throws RestClientException;
    
    Set<HttpMethod> optionsForAllow(final URI p0) throws RestClientException;
    
     <T> ResponseEntity<T> exchange(final String p0, final HttpMethod p1, final HttpEntity<?> p2, final Class<T> p3, final Object... p4) throws RestClientException;
    
     <T> ResponseEntity<T> exchange(final String p0, final HttpMethod p1, final HttpEntity<?> p2, final Class<T> p3, final Map<String, ?> p4) throws RestClientException;
    
     <T> ResponseEntity<T> exchange(final URI p0, final HttpMethod p1, final HttpEntity<?> p2, final Class<T> p3) throws RestClientException;
    
     <T> ResponseEntity<T> exchange(final String p0, final HttpMethod p1, final HttpEntity<?> p2, final ParameterizedTypeReference<T> p3, final Object... p4) throws RestClientException;
    
     <T> ResponseEntity<T> exchange(final String p0, final HttpMethod p1, final HttpEntity<?> p2, final ParameterizedTypeReference<T> p3, final Map<String, ?> p4) throws RestClientException;
    
     <T> ResponseEntity<T> exchange(final URI p0, final HttpMethod p1, final HttpEntity<?> p2, final ParameterizedTypeReference<T> p3) throws RestClientException;
    
     <T> T execute(final String p0, final HttpMethod p1, final RequestCallback p2, final ResponseExtractor<T> p3, final Object... p4) throws RestClientException;
    
     <T> T execute(final String p0, final HttpMethod p1, final RequestCallback p2, final ResponseExtractor<T> p3, final Map<String, ?> p4) throws RestClientException;
    
     <T> T execute(final URI p0, final HttpMethod p1, final RequestCallback p2, final ResponseExtractor<T> p3) throws RestClientException;
}
