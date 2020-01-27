// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import java.io.IOException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.protocol.HttpClientContext;
import org.springframework.http.HttpMethod;
import java.net.URI;
import org.springframework.util.Assert;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.DisposableBean;

public class HttpComponentsClientHttpRequestFactory implements ClientHttpRequestFactory, DisposableBean
{
    private CloseableHttpClient httpClient;
    private int connectTimeout;
    private int socketTimeout;
    private boolean bufferRequestBody;
    
    public HttpComponentsClientHttpRequestFactory() {
        this(HttpClients.createSystem());
    }
    
    public HttpComponentsClientHttpRequestFactory(final HttpClient httpClient) {
        this.bufferRequestBody = true;
        Assert.notNull(httpClient, "'httpClient' must not be null");
        Assert.isInstanceOf(CloseableHttpClient.class, httpClient, "'httpClient' is not of type CloseableHttpClient");
        this.httpClient = (CloseableHttpClient)httpClient;
    }
    
    public void setHttpClient(final HttpClient httpClient) {
        Assert.isInstanceOf(CloseableHttpClient.class, httpClient, "'httpClient' is not of type CloseableHttpClient");
        this.httpClient = (CloseableHttpClient)httpClient;
    }
    
    public HttpClient getHttpClient() {
        return this.httpClient;
    }
    
    public void setConnectTimeout(final int timeout) {
        Assert.isTrue(timeout >= 0, "Timeout must be a non-negative value");
        this.connectTimeout = timeout;
    }
    
    public void setReadTimeout(final int timeout) {
        Assert.isTrue(timeout >= 0, "Timeout must be a non-negative value");
        this.socketTimeout = timeout;
    }
    
    public void setBufferRequestBody(final boolean bufferRequestBody) {
        this.bufferRequestBody = bufferRequestBody;
    }
    
    @Override
    public ClientHttpRequest createRequest(final URI uri, final HttpMethod httpMethod) throws IOException {
        final CloseableHttpClient client = (CloseableHttpClient)this.getHttpClient();
        Assert.state(client != null, "Synchronous execution requires an HttpClient to be set");
        final HttpUriRequest httpRequest = this.createHttpUriRequest(httpMethod, uri);
        this.postProcessHttpRequest(httpRequest);
        HttpContext context = this.createHttpContext(httpMethod, uri);
        if (context == null) {
            context = HttpClientContext.create();
        }
        if (context.getAttribute("http.request-config") == null) {
            RequestConfig config = null;
            if (httpRequest instanceof Configurable) {
                config = ((Configurable)httpRequest).getConfig();
            }
            if (config == null) {
                if (this.socketTimeout > 0 || this.connectTimeout > 0) {
                    config = RequestConfig.custom().setConnectTimeout(this.connectTimeout).setSocketTimeout(this.socketTimeout).build();
                }
                else {
                    config = RequestConfig.DEFAULT;
                }
            }
            context.setAttribute("http.request-config", config);
        }
        if (this.bufferRequestBody) {
            return new HttpComponentsClientHttpRequest(client, httpRequest, context);
        }
        return new HttpComponentsStreamingClientHttpRequest(client, httpRequest, context);
    }
    
    protected HttpUriRequest createHttpUriRequest(final HttpMethod httpMethod, final URI uri) {
        switch (httpMethod) {
            case GET: {
                return new HttpGet(uri);
            }
            case DELETE: {
                return new HttpDelete(uri);
            }
            case HEAD: {
                return new HttpHead(uri);
            }
            case OPTIONS: {
                return new HttpOptions(uri);
            }
            case POST: {
                return new HttpPost(uri);
            }
            case PUT: {
                return new HttpPut(uri);
            }
            case TRACE: {
                return new HttpTrace(uri);
            }
            case PATCH: {
                return new HttpPatch(uri);
            }
            default: {
                throw new IllegalArgumentException("Invalid HTTP method: " + httpMethod);
            }
        }
    }
    
    protected void postProcessHttpRequest(final HttpUriRequest request) {
    }
    
    protected HttpContext createHttpContext(final HttpMethod httpMethod, final URI uri) {
        return null;
    }
    
    @Override
    public void destroy() throws Exception {
        this.httpClient.close();
    }
}
