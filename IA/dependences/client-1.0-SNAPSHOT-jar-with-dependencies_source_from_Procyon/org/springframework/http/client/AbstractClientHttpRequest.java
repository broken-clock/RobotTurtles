// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import org.springframework.util.Assert;
import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.HttpHeaders;

public abstract class AbstractClientHttpRequest implements ClientHttpRequest
{
    private final HttpHeaders headers;
    private boolean executed;
    
    public AbstractClientHttpRequest() {
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
    public final ClientHttpResponse execute() throws IOException {
        this.assertNotExecuted();
        final ClientHttpResponse result = this.executeInternal(this.headers);
        this.executed = true;
        return result;
    }
    
    protected void assertNotExecuted() {
        Assert.state(!this.executed, "ClientHttpRequest already executed");
    }
    
    protected abstract OutputStream getBodyInternal(final HttpHeaders p0) throws IOException;
    
    protected abstract ClientHttpResponse executeInternal(final HttpHeaders p0) throws IOException;
}
