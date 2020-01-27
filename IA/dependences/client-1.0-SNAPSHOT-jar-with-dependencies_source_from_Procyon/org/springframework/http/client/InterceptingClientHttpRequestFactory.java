// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import org.springframework.http.HttpMethod;
import java.net.URI;
import java.util.Collections;
import java.util.List;

public class InterceptingClientHttpRequestFactory extends AbstractClientHttpRequestFactoryWrapper
{
    private final List<ClientHttpRequestInterceptor> interceptors;
    
    public InterceptingClientHttpRequestFactory(final ClientHttpRequestFactory requestFactory, final List<ClientHttpRequestInterceptor> interceptors) {
        super(requestFactory);
        this.interceptors = ((interceptors != null) ? interceptors : Collections.emptyList());
    }
    
    @Override
    protected ClientHttpRequest createRequest(final URI uri, final HttpMethod httpMethod, final ClientHttpRequestFactory requestFactory) {
        return new InterceptingClientHttpRequest(requestFactory, this.interceptors, uri, httpMethod);
    }
}
