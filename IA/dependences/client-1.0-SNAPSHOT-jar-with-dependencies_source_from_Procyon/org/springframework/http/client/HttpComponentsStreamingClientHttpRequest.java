// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import java.io.InputStream;
import org.springframework.http.MediaType;
import org.apache.http.message.BasicHeader;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.HttpHeaders;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.apache.http.protocol.HttpContext;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.http.StreamingHttpOutputMessage;

final class HttpComponentsStreamingClientHttpRequest extends AbstractClientHttpRequest implements StreamingHttpOutputMessage
{
    private final CloseableHttpClient httpClient;
    private final HttpUriRequest httpRequest;
    private final HttpContext httpContext;
    private Body body;
    
    HttpComponentsStreamingClientHttpRequest(final CloseableHttpClient httpClient, final HttpUriRequest httpRequest, final HttpContext httpContext) {
        this.httpClient = httpClient;
        this.httpRequest = httpRequest;
        this.httpContext = httpContext;
    }
    
    @Override
    public HttpMethod getMethod() {
        return HttpMethod.valueOf(this.httpRequest.getMethod());
    }
    
    @Override
    public URI getURI() {
        return this.httpRequest.getURI();
    }
    
    @Override
    public void setBody(final Body body) {
        this.assertNotExecuted();
        this.body = body;
    }
    
    @Override
    protected OutputStream getBodyInternal(final HttpHeaders headers) throws IOException {
        throw new UnsupportedOperationException("getBody not supported");
    }
    
    @Override
    protected ClientHttpResponse executeInternal(final HttpHeaders headers) throws IOException {
        HttpComponentsClientHttpRequest.addHeaders(this.httpRequest, headers);
        if (this.httpRequest instanceof HttpEntityEnclosingRequest && this.body != null) {
            final HttpEntityEnclosingRequest entityEnclosingRequest = (HttpEntityEnclosingRequest)this.httpRequest;
            final HttpEntity requestEntity = new StreamingHttpEntity(this.getHeaders(), this.body);
            entityEnclosingRequest.setEntity(requestEntity);
        }
        final CloseableHttpResponse httpResponse = this.httpClient.execute(this.httpRequest, this.httpContext);
        return new HttpComponentsClientHttpResponse(httpResponse);
    }
    
    private static class StreamingHttpEntity implements HttpEntity
    {
        private final HttpHeaders headers;
        private final Body body;
        
        public StreamingHttpEntity(final HttpHeaders headers, final Body body) {
            this.headers = headers;
            this.body = body;
        }
        
        @Override
        public boolean isRepeatable() {
            return false;
        }
        
        @Override
        public boolean isChunked() {
            return false;
        }
        
        @Override
        public long getContentLength() {
            return this.headers.getContentLength();
        }
        
        @Override
        public Header getContentType() {
            final MediaType contentType = this.headers.getContentType();
            return (contentType != null) ? new BasicHeader("Content-Type", contentType.toString()) : null;
        }
        
        @Override
        public Header getContentEncoding() {
            final String contentEncoding = this.headers.getFirst("Content-Encoding");
            return (contentEncoding != null) ? new BasicHeader("Content-Encoding", contentEncoding) : null;
        }
        
        @Override
        public InputStream getContent() throws IOException, IllegalStateException {
            throw new IllegalStateException("No content available");
        }
        
        @Override
        public void writeTo(final OutputStream outputStream) throws IOException {
            this.body.writeTo(outputStream);
        }
        
        @Override
        public boolean isStreaming() {
            return true;
        }
        
        @Deprecated
        @Override
        public void consumeContent() throws IOException {
            throw new UnsupportedOperationException();
        }
    }
}
