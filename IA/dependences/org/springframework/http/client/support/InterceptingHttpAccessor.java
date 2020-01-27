// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client.support;

import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import java.util.Collection;
import org.springframework.util.CollectionUtils;
import org.springframework.http.client.ClientHttpRequestFactory;
import java.util.ArrayList;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import java.util.List;

public abstract class InterceptingHttpAccessor extends HttpAccessor
{
    private List<ClientHttpRequestInterceptor> interceptors;
    
    public InterceptingHttpAccessor() {
        this.interceptors = new ArrayList<ClientHttpRequestInterceptor>();
    }
    
    public void setInterceptors(final List<ClientHttpRequestInterceptor> interceptors) {
        this.interceptors = interceptors;
    }
    
    public List<ClientHttpRequestInterceptor> getInterceptors() {
        return this.interceptors;
    }
    
    @Override
    public ClientHttpRequestFactory getRequestFactory() {
        final ClientHttpRequestFactory delegate = super.getRequestFactory();
        if (!CollectionUtils.isEmpty(this.getInterceptors())) {
            return new InterceptingClientHttpRequestFactory(delegate, this.getInterceptors());
        }
        return delegate;
    }
}
