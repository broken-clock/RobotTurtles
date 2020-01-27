// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpMethod;
import java.net.URI;

public class BufferingClientHttpRequestFactory extends AbstractClientHttpRequestFactoryWrapper
{
    public BufferingClientHttpRequestFactory(final ClientHttpRequestFactory requestFactory) {
        super(requestFactory);
    }
    
    @Override
    protected ClientHttpRequest createRequest(final URI uri, final HttpMethod httpMethod, final ClientHttpRequestFactory requestFactory) throws IOException {
        final ClientHttpRequest request = requestFactory.createRequest(uri, httpMethod);
        if (this.shouldBuffer(uri, httpMethod)) {
            return new BufferingClientHttpRequestWrapper(request);
        }
        return request;
    }
    
    protected boolean shouldBuffer(final URI uri, final HttpMethod httpMethod) {
        return true;
    }
}
