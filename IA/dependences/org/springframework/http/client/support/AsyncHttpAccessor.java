// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client.support;

import java.io.IOException;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.HttpMethod;
import java.net.URI;
import org.springframework.util.Assert;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.apache.commons.logging.Log;

public class AsyncHttpAccessor
{
    protected final Log logger;
    private AsyncClientHttpRequestFactory asyncRequestFactory;
    
    public AsyncHttpAccessor() {
        this.logger = LogFactory.getLog(this.getClass());
    }
    
    public void setAsyncRequestFactory(final AsyncClientHttpRequestFactory asyncRequestFactory) {
        Assert.notNull(asyncRequestFactory, "'asyncRequestFactory' must not be null");
        this.asyncRequestFactory = asyncRequestFactory;
    }
    
    public AsyncClientHttpRequestFactory getAsyncRequestFactory() {
        return this.asyncRequestFactory;
    }
    
    protected AsyncClientHttpRequest createAsyncRequest(final URI url, final HttpMethod method) throws IOException {
        final AsyncClientHttpRequest request = this.getAsyncRequestFactory().createAsyncRequest(url, method);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Created asynchronous " + method.name() + " request for \"" + url + "\"");
        }
        return request;
    }
}
