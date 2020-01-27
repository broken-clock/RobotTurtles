// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.client;

import java.io.IOException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.protocol.HttpClientContext;
import org.springframework.http.HttpMethod;
import java.net.URI;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.util.Assert;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.springframework.beans.factory.InitializingBean;

public class HttpComponentsAsyncClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory implements AsyncClientHttpRequestFactory, InitializingBean
{
    private CloseableHttpAsyncClient httpAsyncClient;
    
    public HttpComponentsAsyncClientHttpRequestFactory() {
        this(HttpAsyncClients.createSystem());
    }
    
    public HttpComponentsAsyncClientHttpRequestFactory(final CloseableHttpAsyncClient httpAsyncClient) {
        Assert.notNull(httpAsyncClient, "'httpAsyncClient' must not be null");
        this.httpAsyncClient = httpAsyncClient;
    }
    
    public HttpComponentsAsyncClientHttpRequestFactory(final CloseableHttpClient httpClient, final CloseableHttpAsyncClient httpAsyncClient) {
        super(httpClient);
        Assert.notNull(httpAsyncClient, "'httpAsyncClient' must not be null");
        this.httpAsyncClient = httpAsyncClient;
    }
    
    public void setHttpAsyncClient(final CloseableHttpAsyncClient httpAsyncClient) {
        this.httpAsyncClient = httpAsyncClient;
    }
    
    public CloseableHttpAsyncClient getHttpAsyncClient() {
        return this.httpAsyncClient;
    }
    
    @Override
    public void afterPropertiesSet() {
        this.startAsyncClient();
    }
    
    private void startAsyncClient() {
        final CloseableHttpAsyncClient asyncClient = this.getHttpAsyncClient();
        if (!asyncClient.isRunning()) {
            asyncClient.start();
        }
    }
    
    @Override
    public AsyncClientHttpRequest createAsyncRequest(final URI uri, final HttpMethod httpMethod) throws IOException {
        final HttpAsyncClient asyncClient = (HttpAsyncClient)this.getHttpAsyncClient();
        this.startAsyncClient();
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
                config = RequestConfig.DEFAULT;
            }
            context.setAttribute("http.request-config", config);
        }
        return new HttpComponentsAsyncClientHttpRequest(asyncClient, httpRequest, context);
    }
    
    @Override
    public void destroy() throws Exception {
        try {
            super.destroy();
        }
        finally {
            this.getHttpAsyncClient().close();
        }
    }
}
