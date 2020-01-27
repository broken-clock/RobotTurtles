// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import java.io.IOException;
import java.util.Iterator;
import org.springframework.util.FileCopyUtils;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.http.HttpHeaders;
import java.net.URISyntaxException;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import java.net.HttpURLConnection;

final class SimpleBufferingAsyncClientHttpRequest extends AbstractBufferingAsyncClientHttpRequest
{
    private final HttpURLConnection connection;
    private final boolean outputStreaming;
    private final AsyncListenableTaskExecutor taskExecutor;
    
    SimpleBufferingAsyncClientHttpRequest(final HttpURLConnection connection, final boolean outputStreaming, final AsyncListenableTaskExecutor taskExecutor) {
        this.connection = connection;
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
    protected ListenableFuture<ClientHttpResponse> executeInternal(final HttpHeaders headers, final byte[] bufferedOutput) throws IOException {
        return this.taskExecutor.submitListenable((Callable<ClientHttpResponse>)new Callable<ClientHttpResponse>() {
            @Override
            public ClientHttpResponse call() throws Exception {
                for (final Map.Entry<String, List<String>> entry : headers.entrySet()) {
                    final String headerName = entry.getKey();
                    for (final String headerValue : entry.getValue()) {
                        SimpleBufferingAsyncClientHttpRequest.this.connection.addRequestProperty(headerName, headerValue);
                    }
                }
                if (SimpleBufferingAsyncClientHttpRequest.this.connection.getDoOutput() && SimpleBufferingAsyncClientHttpRequest.this.outputStreaming) {
                    SimpleBufferingAsyncClientHttpRequest.this.connection.setFixedLengthStreamingMode(bufferedOutput.length);
                }
                SimpleBufferingAsyncClientHttpRequest.this.connection.connect();
                if (SimpleBufferingAsyncClientHttpRequest.this.connection.getDoOutput()) {
                    FileCopyUtils.copy(bufferedOutput, SimpleBufferingAsyncClientHttpRequest.this.connection.getOutputStream());
                }
                return new SimpleClientHttpResponse(SimpleBufferingAsyncClientHttpRequest.this.connection);
            }
        });
    }
}
