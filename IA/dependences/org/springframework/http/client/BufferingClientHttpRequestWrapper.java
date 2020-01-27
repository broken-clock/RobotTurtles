// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import java.io.IOException;
import org.springframework.util.StreamUtils;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

final class BufferingClientHttpRequestWrapper extends AbstractBufferingClientHttpRequest
{
    private final ClientHttpRequest request;
    
    BufferingClientHttpRequestWrapper(final ClientHttpRequest request) {
        Assert.notNull(request, "'request' must not be null");
        this.request = request;
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
    protected ClientHttpResponse executeInternal(final HttpHeaders headers, final byte[] bufferedOutput) throws IOException {
        this.request.getHeaders().putAll(headers);
        StreamUtils.copy(bufferedOutput, this.request.getBody());
        final ClientHttpResponse response = this.request.execute();
        return new BufferingClientHttpResponseWrapper(response);
    }
}
