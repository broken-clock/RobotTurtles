// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.httpinvoker;

import java.util.zip.GZIPInputStream;
import java.util.Locale;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.util.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import java.net.URLConnection;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import org.springframework.remoting.support.RemoteInvocationResult;
import java.io.ByteArrayOutputStream;

public class SimpleHttpInvokerRequestExecutor extends AbstractHttpInvokerRequestExecutor
{
    private int connectTimeout;
    private int readTimeout;
    
    public SimpleHttpInvokerRequestExecutor() {
        this.connectTimeout = -1;
        this.readTimeout = -1;
    }
    
    public void setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    
    public void setReadTimeout(final int readTimeout) {
        this.readTimeout = readTimeout;
    }
    
    @Override
    protected RemoteInvocationResult doExecuteRequest(final HttpInvokerClientConfiguration config, final ByteArrayOutputStream baos) throws IOException, ClassNotFoundException {
        final HttpURLConnection con = this.openConnection(config);
        this.prepareConnection(con, baos.size());
        this.writeRequestBody(config, con, baos);
        this.validateResponse(config, con);
        final InputStream responseBody = this.readResponseBody(config, con);
        return this.readRemoteInvocationResult(responseBody, config.getCodebaseUrl());
    }
    
    protected HttpURLConnection openConnection(final HttpInvokerClientConfiguration config) throws IOException {
        final URLConnection con = new URL(config.getServiceUrl()).openConnection();
        if (!(con instanceof HttpURLConnection)) {
            throw new IOException("Service URL [" + config.getServiceUrl() + "] is not an HTTP URL");
        }
        return (HttpURLConnection)con;
    }
    
    protected void prepareConnection(final HttpURLConnection connection, final int contentLength) throws IOException {
        if (this.connectTimeout >= 0) {
            connection.setConnectTimeout(this.connectTimeout);
        }
        if (this.readTimeout >= 0) {
            connection.setReadTimeout(this.readTimeout);
        }
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", this.getContentType());
        connection.setRequestProperty("Content-Length", Integer.toString(contentLength));
        final LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
        if (localeContext != null) {
            final Locale locale = localeContext.getLocale();
            if (locale != null) {
                connection.setRequestProperty("Accept-Language", StringUtils.toLanguageTag(locale));
            }
        }
        if (this.isAcceptGzipEncoding()) {
            connection.setRequestProperty("Accept-Encoding", "gzip");
        }
    }
    
    protected void writeRequestBody(final HttpInvokerClientConfiguration config, final HttpURLConnection con, final ByteArrayOutputStream baos) throws IOException {
        baos.writeTo(con.getOutputStream());
    }
    
    protected void validateResponse(final HttpInvokerClientConfiguration config, final HttpURLConnection con) throws IOException {
        if (con.getResponseCode() >= 300) {
            throw new IOException("Did not receive successful HTTP response: status code = " + con.getResponseCode() + ", status message = [" + con.getResponseMessage() + "]");
        }
    }
    
    protected InputStream readResponseBody(final HttpInvokerClientConfiguration config, final HttpURLConnection con) throws IOException {
        if (this.isGzipResponse(con)) {
            return new GZIPInputStream(con.getInputStream());
        }
        return con.getInputStream();
    }
    
    protected boolean isGzipResponse(final HttpURLConnection con) {
        final String encodingHeader = con.getHeaderField("Content-Encoding");
        return encodingHeader != null && encodingHeader.toLowerCase().contains("gzip");
    }
}
