// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import org.apache.http.HttpEntity;
import java.io.InputStream;
import org.apache.http.Header;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.apache.http.HttpResponse;

final class HttpComponentsAsyncClientHttpResponse extends AbstractClientHttpResponse
{
    private final HttpResponse httpResponse;
    private HttpHeaders headers;
    
    HttpComponentsAsyncClientHttpResponse(final HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }
    
    @Override
    public int getRawStatusCode() throws IOException {
        return this.httpResponse.getStatusLine().getStatusCode();
    }
    
    @Override
    public String getStatusText() throws IOException {
        return this.httpResponse.getStatusLine().getReasonPhrase();
    }
    
    @Override
    public HttpHeaders getHeaders() {
        if (this.headers == null) {
            this.headers = new HttpHeaders();
            for (final Header header : this.httpResponse.getAllHeaders()) {
                this.headers.add(header.getName(), header.getValue());
            }
        }
        return this.headers;
    }
    
    @Override
    public InputStream getBody() throws IOException {
        final HttpEntity entity = this.httpResponse.getEntity();
        return (entity != null) ? entity.getContent() : null;
    }
    
    @Override
    public void close() {
    }
}
