// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import org.springframework.util.StreamUtils;
import java.util.Map;
import java.util.Iterator;
import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpHeaders;
import java.net.URI;
import org.springframework.http.HttpMethod;
import java.util.List;

class InterceptingClientHttpRequest extends AbstractBufferingClientHttpRequest
{
    private final ClientHttpRequestFactory requestFactory;
    private final List<ClientHttpRequestInterceptor> interceptors;
    private HttpMethod method;
    private URI uri;
    
    protected InterceptingClientHttpRequest(final ClientHttpRequestFactory requestFactory, final List<ClientHttpRequestInterceptor> interceptors, final URI uri, final HttpMethod method) {
        this.requestFactory = requestFactory;
        this.interceptors = interceptors;
        this.method = method;
        this.uri = uri;
    }
    
    @Override
    public HttpMethod getMethod() {
        return this.method;
    }
    
    @Override
    public URI getURI() {
        return this.uri;
    }
    
    @Override
    protected final ClientHttpResponse executeInternal(final HttpHeaders headers, final byte[] bufferedOutput) throws IOException {
        final RequestExecution requestExecution = new RequestExecution();
        return requestExecution.execute(this, bufferedOutput);
    }
    
    private class RequestExecution implements ClientHttpRequestExecution
    {
        private final Iterator<ClientHttpRequestInterceptor> iterator;
        
        private RequestExecution() {
            this.iterator = InterceptingClientHttpRequest.this.interceptors.iterator();
        }
        
        @Override
        public ClientHttpResponse execute(final HttpRequest request, final byte[] body) throws IOException {
            if (this.iterator.hasNext()) {
                final ClientHttpRequestInterceptor nextInterceptor = this.iterator.next();
                return nextInterceptor.intercept(request, body, this);
            }
            final ClientHttpRequest delegate = InterceptingClientHttpRequest.this.requestFactory.createRequest(request.getURI(), request.getMethod());
            delegate.getHeaders().putAll(request.getHeaders());
            if (body.length > 0) {
                StreamUtils.copy(body, delegate.getBody());
            }
            return delegate.execute();
        }
    }
}
