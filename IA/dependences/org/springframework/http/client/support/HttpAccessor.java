// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client.support;

import java.io.IOException;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.HttpMethod;
import java.net.URI;
import org.springframework.util.Assert;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.apache.commons.logging.Log;

public abstract class HttpAccessor
{
    protected final Log logger;
    private ClientHttpRequestFactory requestFactory;
    
    public HttpAccessor() {
        this.logger = LogFactory.getLog(this.getClass());
        this.requestFactory = new SimpleClientHttpRequestFactory();
    }
    
    public void setRequestFactory(final ClientHttpRequestFactory requestFactory) {
        Assert.notNull(requestFactory, "'requestFactory' must not be null");
        this.requestFactory = requestFactory;
    }
    
    public ClientHttpRequestFactory getRequestFactory() {
        return this.requestFactory;
    }
    
    protected ClientHttpRequest createRequest(final URI url, final HttpMethod method) throws IOException {
        final ClientHttpRequest request = this.getRequestFactory().createRequest(url, method);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Created " + method.name() + " request for \"" + url + "\"");
        }
        return request;
    }
}
