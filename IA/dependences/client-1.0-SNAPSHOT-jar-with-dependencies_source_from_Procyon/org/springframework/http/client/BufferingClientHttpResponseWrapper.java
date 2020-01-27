// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import java.io.ByteArrayInputStream;
import org.springframework.util.StreamUtils;
import java.io.InputStream;
import org.springframework.http.HttpHeaders;
import java.io.IOException;
import org.springframework.http.HttpStatus;

final class BufferingClientHttpResponseWrapper implements ClientHttpResponse
{
    private final ClientHttpResponse response;
    private byte[] body;
    
    BufferingClientHttpResponseWrapper(final ClientHttpResponse response) {
        this.response = response;
    }
    
    @Override
    public HttpStatus getStatusCode() throws IOException {
        return this.response.getStatusCode();
    }
    
    @Override
    public int getRawStatusCode() throws IOException {
        return this.response.getRawStatusCode();
    }
    
    @Override
    public String getStatusText() throws IOException {
        return this.response.getStatusText();
    }
    
    @Override
    public HttpHeaders getHeaders() {
        return this.response.getHeaders();
    }
    
    @Override
    public InputStream getBody() throws IOException {
        if (this.body == null) {
            this.body = StreamUtils.copyToByteArray(this.response.getBody());
        }
        return new ByteArrayInputStream(this.body);
    }
    
    @Override
    public void close() {
        this.response.close();
    }
}
