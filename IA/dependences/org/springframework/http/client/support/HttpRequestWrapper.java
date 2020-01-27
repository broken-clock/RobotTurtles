// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client.support;

import org.springframework.http.HttpHeaders;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.http.HttpRequest;

public class HttpRequestWrapper implements HttpRequest
{
    private final HttpRequest request;
    
    public HttpRequestWrapper(final HttpRequest request) {
        Assert.notNull(request, "'request' must not be null");
        this.request = request;
    }
    
    public HttpRequest getRequest() {
        return this.request;
    }
    
    @Override
    public HttpMethod getMethod() {
        return this.request.getMethod();
    }
    
    @Override
    public URI getURI() {
        return this.request.getURI();
    }
    
    @Override
    public HttpHeaders getHeaders() {
        return this.request.getHeaders();
    }
}
