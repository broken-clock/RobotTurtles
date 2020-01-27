// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import java.util.concurrent.Callable;
import org.springframework.util.concurrent.ListenableFuture;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import org.springframework.util.StreamUtils;
import org.springframework.http.HttpHeaders;
import java.net.URISyntaxException;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import java.io.OutputStream;
import java.net.HttpURLConnection;

final class SimpleStreamingAsyncClientHttpRequest extends AbstractAsyncClientHttpRequest
{
    private final HttpURLConnection connection;
    private final int chunkSize;
    private OutputStream body;
    private final boolean outputStreaming;
    private final AsyncListenableTaskExecutor taskExecutor;
    
    SimpleStreamingAsyncClientHttpRequest(final HttpURLConnection connection, final int chunkSize, final boolean outputStreaming, final AsyncListenableTaskExecutor taskExecutor) {
        this.connection = connection;
        this.chunkSize = chunkSize;
        this.outputStreaming = outputStreaming;
        this.taskExecutor = taskExecutor;
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
    protected OutputStream getBodyInternal(final HttpHeaders headers) throws IOException {
        if (this.body == null) {
            if (this.outputStreaming) {
                final int contentLength = (int)headers.getContentLength();
                if (contentLength >= 0) {
                    this.connection.setFixedLengthStreamingMode(contentLength);
                }
                else {
                    this.connection.setChunkedStreamingMode(this.chunkSize);
                }
            }
            this.writeHeaders(headers);
            this.connection.connect();
            this.body = this.connection.getOutputStream();
        }
        return StreamUtils.nonClosing(this.body);
    }
    
    private void writeHeaders(final HttpHeaders headers) {
        for (final Map.Entry<String, List<String>> entry : headers.entrySet()) {
            final String headerName = entry.getKey();
            for (final String headerValue : entry.getValue()) {
                this.connection.addRequestProperty(headerName, headerValue);
            }
        }
    }
    
    @Override
    protected ListenableFuture<ClientHttpResponse> executeInternal(final HttpHeaders headers) throws IOException {
        return this.taskExecutor.submitListenable((Callable<ClientHttpResponse>)new Callable<ClientHttpResponse>() {
            @Override
            public ClientHttpResponse call() throws Exception {
                try {
                    if (SimpleStreamingAsyncClientHttpRequest.this.body != null) {
                        SimpleStreamingAsyncClientHttpRequest.this.body.close();
                    }
                    else {
                        SimpleStreamingAsyncClientHttpRequest.this.writeHeaders(headers);
                        SimpleStreamingAsyncClientHttpRequest.this.connection.connect();
                    }
                }
                catch (IOException ex) {}
                return new SimpleClientHttpResponse(SimpleStreamingAsyncClientHttpRequest.this.connection);
            }
        });
    }
}
