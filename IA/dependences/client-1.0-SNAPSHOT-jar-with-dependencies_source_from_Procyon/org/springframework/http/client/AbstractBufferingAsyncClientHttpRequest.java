// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import org.springframework.util.concurrent.ListenableFuture;
import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.HttpHeaders;
import java.io.ByteArrayOutputStream;

abstract class AbstractBufferingAsyncClientHttpRequest extends AbstractAsyncClientHttpRequest
{
    private ByteArrayOutputStream bufferedOutput;
    
    AbstractBufferingAsyncClientHttpRequest() {
        this.bufferedOutput = new ByteArrayOutputStream();
    }
    
    @Override
    protected OutputStream getBodyInternal(final HttpHeaders headers) throws IOException {
        return this.bufferedOutput;
    }
    
    @Override
    protected ListenableFuture<ClientHttpResponse> executeInternal(final HttpHeaders headers) throws IOException {
        final byte[] bytes = this.bufferedOutput.toByteArray();
        if (headers.getContentLength() == -1L) {
            headers.setContentLength(bytes.length);
        }
        final ListenableFuture<ClientHttpResponse> result = this.executeInternal(headers, bytes);
        this.bufferedOutput = null;
        return result;
    }
    
    protected abstract ListenableFuture<ClientHttpResponse> executeInternal(final HttpHeaders p0, final byte[] p1) throws IOException;
}
