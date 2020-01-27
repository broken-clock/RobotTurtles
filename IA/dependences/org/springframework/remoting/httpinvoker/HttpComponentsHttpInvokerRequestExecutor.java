// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.httpinvoker;

import org.apache.http.Header;
import java.util.zip.GZIPInputStream;
import org.apache.http.StatusLine;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import java.util.Locale;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.util.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.springframework.remoting.support.RemoteInvocationResult;
import java.io.ByteArrayOutputStream;
import org.springframework.util.Assert;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.client.HttpClient;

public class HttpComponentsHttpInvokerRequestExecutor extends AbstractHttpInvokerRequestExecutor
{
    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 100;
    private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 5;
    private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = 60000;
    private HttpClient httpClient;
    
    public HttpComponentsHttpInvokerRequestExecutor() {
        final SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
        final PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager(schemeRegistry);
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(5);
        this.httpClient = new DefaultHttpClient(connectionManager);
        this.setReadTimeout(60000);
    }
    
    public HttpComponentsHttpInvokerRequestExecutor(final HttpClient httpClient) {
        this.httpClient = httpClient;
    }
    
    public void setHttpClient(final HttpClient httpClient) {
        this.httpClient = httpClient;
    }
    
    public HttpClient getHttpClient() {
        return this.httpClient;
    }
    
    public void setConnectTimeout(final int timeout) {
        Assert.isTrue(timeout >= 0, "Timeout must be a non-negative value");
        this.getHttpClient().getParams().setIntParameter("http.connection.timeout", timeout);
    }
    
    public void setReadTimeout(final int timeout) {
        Assert.isTrue(timeout >= 0, "Timeout must be a non-negative value");
        this.getHttpClient().getParams().setIntParameter("http.socket.timeout", timeout);
    }
    
    @Override
    protected RemoteInvocationResult doExecuteRequest(final HttpInvokerClientConfiguration config, final ByteArrayOutputStream baos) throws IOException, ClassNotFoundException {
        final HttpPost postMethod = this.createHttpPost(config);
        this.setRequestBody(config, postMethod, baos);
        try {
            final HttpResponse response = this.executeHttpPost(config, this.getHttpClient(), postMethod);
            this.validateResponse(config, response);
            final InputStream responseBody = this.getResponseBody(config, response);
            return this.readRemoteInvocationResult(responseBody, config.getCodebaseUrl());
        }
        finally {
            postMethod.releaseConnection();
        }
    }
    
    protected HttpPost createHttpPost(final HttpInvokerClientConfiguration config) throws IOException {
        final HttpPost httpPost = new HttpPost(config.getServiceUrl());
        final LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
        if (localeContext != null) {
            final Locale locale = localeContext.getLocale();
            if (locale != null) {
                httpPost.addHeader("Accept-Language", StringUtils.toLanguageTag(locale));
            }
        }
        if (this.isAcceptGzipEncoding()) {
            httpPost.addHeader("Accept-Encoding", "gzip");
        }
        return httpPost;
    }
    
    protected void setRequestBody(final HttpInvokerClientConfiguration config, final HttpPost httpPost, final ByteArrayOutputStream baos) throws IOException {
        final ByteArrayEntity entity = new ByteArrayEntity(baos.toByteArray());
        entity.setContentType(this.getContentType());
        httpPost.setEntity(entity);
    }
    
    protected HttpResponse executeHttpPost(final HttpInvokerClientConfiguration config, final HttpClient httpClient, final HttpPost httpPost) throws IOException {
        return httpClient.execute(httpPost);
    }
    
    protected void validateResponse(final HttpInvokerClientConfiguration config, final HttpResponse response) throws IOException {
        final StatusLine status = response.getStatusLine();
        if (status.getStatusCode() >= 300) {
            throw new NoHttpResponseException("Did not receive successful HTTP response: status code = " + status.getStatusCode() + ", status message = [" + status.getReasonPhrase() + "]");
        }
    }
    
    protected InputStream getResponseBody(final HttpInvokerClientConfiguration config, final HttpResponse httpResponse) throws IOException {
        if (this.isGzipResponse(httpResponse)) {
            return new GZIPInputStream(httpResponse.getEntity().getContent());
        }
        return httpResponse.getEntity().getContent();
    }
    
    protected boolean isGzipResponse(final HttpResponse httpResponse) {
        final Header encodingHeader = httpResponse.getFirstHeader("Content-Encoding");
        return encodingHeader != null && encodingHeader.getValue() != null && encodingHeader.getValue().toLowerCase().contains("gzip");
    }
}
