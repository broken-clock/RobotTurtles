// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpMethod;
import java.net.URI;
import org.springframework.util.Assert;

public abstract class AbstractClientHttpRequestFactoryWrapper implements ClientHttpRequestFactory
{
    private final ClientHttpRequestFactory requestFactory;
    
    protected AbstractClientHttpRequestFactoryWrapper(final ClientHttpRequestFactory requestFactory) {
        Assert.notNull(requestFactory, "'requestFactory' must not be null");
        this.requestFactory = requestFactory;
    }
    
    @Override
    public final ClientHttpRequest createRequest(final URI uri, final HttpMethod httpMethod) throws IOException {
        return this.createRequest(uri, httpMethod, this.requestFactory);
    }
    
    protected abstract ClientHttpRequest createRequest(final URI p0, final HttpMethod p1, final ClientHttpRequestFactory p2) throws IOException;
}
