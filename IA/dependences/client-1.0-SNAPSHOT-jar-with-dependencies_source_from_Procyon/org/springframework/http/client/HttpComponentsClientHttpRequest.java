// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.springframework.http.HttpHeaders;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.apache.http.protocol.HttpContext;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;

final class HttpComponentsClientHttpRequest extends AbstractBufferingClientHttpRequest
{
    private final CloseableHttpClient httpClient;
    private final HttpUriRequest httpRequest;
    private final HttpContext httpContext;
    
    HttpComponentsClientHttpRequest(final CloseableHttpClient httpClient, final HttpUriRequest httpRequest, final HttpContext httpContext) {
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
    protected ClientHttpResponse executeInternal(final HttpHeaders headers, final byte[] bufferedOutput) throws IOException {
        addHeaders(this.httpRequest, headers);
        if (this.httpRequest instanceof HttpEntityEnclosingRequest) {
            final HttpEntityEnclosingRequest entityEnclosingRequest = (HttpEntityEnclosingRequest)this.httpRequest;
            final HttpEntity requestEntity = new ByteArrayEntity(bufferedOutput);
            entityEnclosingRequest.setEntity(requestEntity);
        }
        final CloseableHttpResponse httpResponse = this.httpClient.execute(this.httpRequest, this.httpContext);
        return new HttpComponentsClientHttpResponse(httpResponse);
    }
    
    static void addHeaders(final HttpUriRequest httpRequest, final HttpHeaders headers) {
        for (final Map.Entry<String, List<String>> entry : headers.entrySet()) {
            final String headerName = entry.getKey();
            if (!headerName.equalsIgnoreCase("Content-Length") && !headerName.equalsIgnoreCase("Transfer-Encoding")) {
                for (final String headerValue : entry.getValue()) {
                    httpRequest.addHeader(headerName, headerValue);
                }
            }
        }
    }
}
