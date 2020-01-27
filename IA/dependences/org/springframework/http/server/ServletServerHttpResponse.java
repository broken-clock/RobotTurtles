// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.server;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.http.HttpHeaders;
import javax.servlet.http.HttpServletResponse;

public class ServletServerHttpResponse implements ServerHttpResponse
{
    private final HttpServletResponse servletResponse;
    private final HttpHeaders headers;
    private boolean headersWritten;
    
    public ServletServerHttpResponse(final HttpServletResponse servletResponse) {
        this.headers = new HttpHeaders();
        this.headersWritten = false;
        Assert.notNull(servletResponse, "'servletResponse' must not be null");
        this.servletResponse = servletResponse;
    }
    
    public HttpServletResponse getServletResponse() {
        return this.servletResponse;
    }
    
    @Override
    public void setStatusCode(final HttpStatus status) {
        this.servletResponse.setStatus(status.value());
    }
    
    @Override
    public HttpHeaders getHeaders() {
        return this.headersWritten ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers;
    }
    
    @Override
    public OutputStream getBody() throws IOException {
        this.writeHeaders();
        return (OutputStream)this.servletResponse.getOutputStream();
    }
    
    @Override
    public void flush() throws IOException {
        this.writeHeaders();
        this.servletResponse.flushBuffer();
    }
    
    @Override
    public void close() {
        this.writeHeaders();
    }
    
    private void writeHeaders() {
        if (!this.headersWritten) {
            for (final Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
                final String headerName = entry.getKey();
                for (final String headerValue : entry.getValue()) {
                    this.servletResponse.addHeader(headerName, headerValue);
                }
            }
            if (this.servletResponse.getContentType() == null && this.headers.getContentType() != null) {
                this.servletResponse.setContentType(this.headers.getContentType().toString());
            }
            if (this.servletResponse.getCharacterEncoding() == null && this.headers.getContentType() != null && this.headers.getContentType().getCharSet() != null) {
                this.servletResponse.setCharacterEncoding(this.headers.getContentType().getCharSet().name());
            }
            this.headersWritten = true;
        }
    }
}
