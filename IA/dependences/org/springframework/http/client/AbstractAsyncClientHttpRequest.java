// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.HttpHeaders;

abstract class AbstractAsyncClientHttpRequest implements AsyncClientHttpRequest
{
    private final HttpHeaders headers;
    private boolean executed;
    
    AbstractAsyncClientHttpRequest() {
        this.headers = new HttpHeaders();
        this.executed = false;
    }
    
    @Override
    public final HttpHeaders getHeaders() {
        return this.executed ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers;
    }
    
    @Override
    public final OutputStream getBody() throws IOException {
        this.assertNotExecuted();
        return this.getBodyInternal(this.headers);
    }
    
    @Override
    public ListenableFuture<ClientHttpResponse> executeAsync() throws IOException {
        this.assertNotExecuted();
        final ListenableFuture<ClientHttpResponse> result = this.executeInternal(this.headers);
        this.executed = true;
        return result;
    }
    
    protected void assertNotExecuted() {
        Assert.state(!this.executed, "ClientHttpRequest already executed");
    }
    
    protected abstract OutputStream getBodyInternal(final HttpHeaders p0) throws IOException;
    
    protected abstract ListenableFuture<ClientHttpResponse> executeInternal(final HttpHeaders p0) throws IOException;
}
