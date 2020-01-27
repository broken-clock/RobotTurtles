// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import java.io.IOException;
import java.util.Iterator;
import org.springframework.util.FileCopyUtils;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import java.net.URISyntaxException;
import java.net.URI;
import org.springframework.http.HttpMethod;
import java.net.HttpURLConnection;

final class SimpleBufferingClientHttpRequest extends AbstractBufferingClientHttpRequest
{
    private final HttpURLConnection connection;
    private final boolean outputStreaming;
    
    SimpleBufferingClientHttpRequest(final HttpURLConnection connection, final boolean outputStreaming) {
        this.connection = connection;
        this.outputStreaming = outputStreaming;
    }
    
    @Override
    public HttpMethod getMethod() {
        return HttpMethod.valueOf(this.connection.getRequestMethod());
    }
    
    @Override
    public URI getURI() {
        try {
            return this.connection.getURL().toURI();
        }
        catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not get HttpURLConnection URI: " + ex.getMessage(), ex);
        }
    }
    
    @Override
    protected ClientHttpResponse executeInternal(final HttpHeaders headers, final byte[] bufferedOutput) throws IOException {
        for (final Map.Entry<String, List<String>> entry : headers.entrySet()) {
            final String headerName = entry.getKey();
            for (final String headerValue : entry.getValue()) {
                this.connection.addRequestProperty(headerName, headerValue);
            }
        }
        if (this.connection.getDoOutput() && this.outputStreaming) {
            this.connection.setFixedLengthStreamingMode(bufferedOutput.length);
        }
        this.connection.connect();
        if (this.connection.getDoOutput()) {
            FileCopyUtils.copy(bufferedOutput, this.connection.getOutputStream());
        }
        return new SimpleClientHttpResponse(this.connection);
    }
}
