// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import java.io.InputStream;
import org.springframework.util.StringUtils;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import java.net.HttpURLConnection;

final class SimpleClientHttpResponse extends AbstractClientHttpResponse
{
    private final HttpURLConnection connection;
    private HttpHeaders headers;
    
    SimpleClientHttpResponse(final HttpURLConnection connection) {
        this.connection = connection;
    }
    
    @Override
    public int getRawStatusCode() throws IOException {
        return this.connection.getResponseCode();
    }
    
    @Override
    public String getStatusText() throws IOException {
        return this.connection.getResponseMessage();
    }
    
    @Override
    public HttpHeaders getHeaders() {
        if (this.headers == null) {
            this.headers = new HttpHeaders();
            String name = this.connection.getHeaderFieldKey(0);
            if (StringUtils.hasLength(name)) {
                this.headers.add(name, this.connection.getHeaderField(0));
            }
            int i = 1;
            while (true) {
                name = this.connection.getHeaderFieldKey(i);
                if (!StringUtils.hasLength(name)) {
                    break;
                }
                this.headers.add(name, this.connection.getHeaderField(i));
                ++i;
            }
        }
        return this.headers;
    }
    
    @Override
    public InputStream getBody() throws IOException {
        final InputStream errorStream = this.connection.getErrorStream();
        return (errorStream != null) ? errorStream : this.connection.getInputStream();
    }
    
    @Override
    public void close() {
        this.connection.disconnect();
    }
}
