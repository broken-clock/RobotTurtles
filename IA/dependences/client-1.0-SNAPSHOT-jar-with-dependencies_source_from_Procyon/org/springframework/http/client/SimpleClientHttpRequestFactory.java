// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import java.net.URLConnection;
import java.net.URL;
import org.springframework.util.Assert;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.springframework.http.HttpMethod;
import java.net.URI;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import java.net.Proxy;

public class SimpleClientHttpRequestFactory implements ClientHttpRequestFactory, AsyncClientHttpRequestFactory
{
    private static final int DEFAULT_CHUNK_SIZE = 4096;
    private Proxy proxy;
    private boolean bufferRequestBody;
    private int chunkSize;
    private int connectTimeout;
    private int readTimeout;
    private boolean outputStreaming;
    private AsyncListenableTaskExecutor taskExecutor;
    
    public SimpleClientHttpRequestFactory() {
        this.bufferRequestBody = true;
        this.chunkSize = 4096;
        this.connectTimeout = -1;
        this.readTimeout = -1;
        this.outputStreaming = true;
    }
    
    public void setProxy(final Proxy proxy) {
        this.proxy = proxy;
    }
    
    public void setBufferRequestBody(final boolean bufferRequestBody) {
        this.bufferRequestBody = bufferRequestBody;
    }
    
    public void setChunkSize(final int chunkSize) {
        this.chunkSize = chunkSize;
    }
    
    public void setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    
    public void setReadTimeout(final int readTimeout) {
        this.readTimeout = readTimeout;
    }
    
    public void setOutputStreaming(final boolean outputStreaming) {
        this.outputStreaming = outputStreaming;
    }
    
    public void setTaskExecutor(final AsyncListenableTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
    
    @Override
    public ClientHttpRequest createRequest(final URI uri, final HttpMethod httpMethod) throws IOException {
        final HttpURLConnection connection = this.openConnection(uri.toURL(), this.proxy);
        this.prepareConnection(connection, httpMethod.name());
        if (this.bufferRequestBody) {
            return new SimpleBufferingClientHttpRequest(connection, this.outputStreaming);
        }
        return new SimpleStreamingClientHttpRequest(connection, this.chunkSize, this.outputStreaming);
    }
    
    @Override
    public AsyncClientHttpRequest createAsyncRequest(final URI uri, final HttpMethod httpMethod) throws IOException {
        Assert.state(this.taskExecutor != null, "Asynchronous execution requires an AsyncTaskExecutor to be set");
        final HttpURLConnection connection = this.openConnection(uri.toURL(), this.proxy);
        this.prepareConnection(connection, httpMethod.name());
        if (this.bufferRequestBody) {
            return new SimpleBufferingAsyncClientHttpRequest(connection, this.outputStreaming, this.taskExecutor);
        }
        return new SimpleStreamingAsyncClientHttpRequest(connection, this.chunkSize, this.outputStreaming, this.taskExecutor);
    }
    
    protected HttpURLConnection openConnection(final URL url, final Proxy proxy) throws IOException {
        final URLConnection urlConnection = (proxy != null) ? url.openConnection(proxy) : url.openConnection();
        Assert.isInstanceOf(HttpURLConnection.class, urlConnection);
        return (HttpURLConnection)urlConnection;
    }
    
    protected void prepareConnection(final HttpURLConnection connection, final String httpMethod) throws IOException {
        if (this.connectTimeout >= 0) {
            connection.setConnectTimeout(this.connectTimeout);
        }
        if (this.readTimeout >= 0) {
            connection.setReadTimeout(this.readTimeout);
        }
        connection.setDoInput(true);
        if ("GET".equals(httpMethod)) {
            connection.setInstanceFollowRedirects(true);
        }
        else {
            connection.setInstanceFollowRedirects(false);
        }
        if ("PUT".equals(httpMethod) || "POST".equals(httpMethod) || "PATCH".equals(httpMethod)) {
            connection.setDoOutput(true);
        }
        else {
            connection.setDoOutput(false);
        }
        connection.setRequestMethod(httpMethod);
    }
}
