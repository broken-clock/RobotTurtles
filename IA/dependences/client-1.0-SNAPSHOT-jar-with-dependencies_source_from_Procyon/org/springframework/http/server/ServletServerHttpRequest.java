// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.server;

import java.util.List;
import java.util.Iterator;
import java.io.Writer;
import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;
import java.nio.charset.Charset;
import org.springframework.http.MediaType;
import java.net.URISyntaxException;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.http.HttpHeaders;
import javax.servlet.http.HttpServletRequest;

public class ServletServerHttpRequest implements ServerHttpRequest
{
    protected static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";
    protected static final String FORM_CHARSET = "UTF-8";
    private static final String METHOD_POST = "POST";
    private final HttpServletRequest servletRequest;
    private HttpHeaders headers;
    private ServerHttpAsyncRequestControl asyncRequestControl;
    
    public ServletServerHttpRequest(final HttpServletRequest servletRequest) {
        Assert.notNull(servletRequest, "'servletRequest' must not be null");
        this.servletRequest = servletRequest;
    }
    
    public HttpServletRequest getServletRequest() {
        return this.servletRequest;
    }
    
    @Override
    public HttpMethod getMethod() {
        return HttpMethod.valueOf(this.servletRequest.getMethod());
    }
    
    @Override
    public URI getURI() {
        try {
            return new URI(this.servletRequest.getScheme(), null, this.servletRequest.getServerName(), this.servletRequest.getServerPort(), this.servletRequest.getRequestURI(), this.servletRequest.getQueryString(), null);
        }
        catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not get HttpServletRequest URI: " + ex.getMessage(), ex);
        }
    }
    
    @Override
    public HttpHeaders getHeaders() {
        if (this.headers == null) {
            this.headers = new HttpHeaders();
            final Enumeration<?> headerNames = (Enumeration<?>)this.servletRequest.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                final String headerName = (String)headerNames.nextElement();
                final Enumeration<?> headerValues = (Enumeration<?>)this.servletRequest.getHeaders(headerName);
                while (headerValues.hasMoreElements()) {
                    final String headerValue = (String)headerValues.nextElement();
                    this.headers.add(headerName, headerValue);
                }
            }
            if (this.headers.getContentType() == null && this.servletRequest.getContentType() != null) {
                final MediaType contentType = MediaType.parseMediaType(this.servletRequest.getContentType());
                this.headers.setContentType(contentType);
            }
            if (this.headers.getContentType() != null && this.headers.getContentType().getCharSet() == null && this.servletRequest.getCharacterEncoding() != null) {
                final MediaType oldContentType = this.headers.getContentType();
                final Charset charSet = Charset.forName(this.servletRequest.getCharacterEncoding());
                final Map<String, String> params = new HashMap<String, String>(oldContentType.getParameters());
                params.put("charset", charSet.toString());
                final MediaType newContentType = new MediaType(oldContentType.getType(), oldContentType.getSubtype(), params);
                this.headers.setContentType(newContentType);
            }
            if (this.headers.getContentLength() == -1L && this.servletRequest.getContentLength() != -1) {
                this.headers.setContentLength(this.servletRequest.getContentLength());
            }
        }
        return this.headers;
    }
    
    @Override
    public Principal getPrincipal() {
        return this.servletRequest.getUserPrincipal();
    }
    
    @Override
    public InetSocketAddress getLocalAddress() {
        return new InetSocketAddress(this.servletRequest.getLocalName(), this.servletRequest.getLocalPort());
    }
    
    @Override
    public InetSocketAddress getRemoteAddress() {
        return new InetSocketAddress(this.servletRequest.getRemoteHost(), this.servletRequest.getRemotePort());
    }
    
    @Override
    public InputStream getBody() throws IOException {
        if (this.isFormPost(this.servletRequest)) {
            return this.getBodyFromServletRequestParameters(this.servletRequest);
        }
        return (InputStream)this.servletRequest.getInputStream();
    }
    
    private boolean isFormPost(final HttpServletRequest request) {
        return request.getContentType() != null && request.getContentType().contains("application/x-www-form-urlencoded") && "POST".equalsIgnoreCase(request.getMethod());
    }
    
    private InputStream getBodyFromServletRequestParameters(final HttpServletRequest request) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final Writer writer = new OutputStreamWriter(bos, "UTF-8");
        final Map<String, String[]> form = (Map<String, String[]>)request.getParameterMap();
        final Iterator<String> nameIterator = form.keySet().iterator();
        while (nameIterator.hasNext()) {
            final String name = nameIterator.next();
            final List<String> values = Arrays.asList((String[])form.get(name));
            final Iterator<String> valueIterator = values.iterator();
            while (valueIterator.hasNext()) {
                final String value = valueIterator.next();
                writer.write(URLEncoder.encode(name, "UTF-8"));
                if (value != null) {
                    writer.write(61);
                    writer.write(URLEncoder.encode(value, "UTF-8"));
                    if (!valueIterator.hasNext()) {
                        continue;
                    }
                    writer.write(38);
                }
            }
            if (nameIterator.hasNext()) {
                writer.append('&');
            }
        }
        writer.flush();
        return new ByteArrayInputStream(bos.toByteArray());
    }
    
    @Override
    public ServerHttpAsyncRequestControl getAsyncRequestControl(final ServerHttpResponse response) {
        if (this.asyncRequestControl == null) {
            Assert.isInstanceOf(ServletServerHttpResponse.class, response);
            final ServletServerHttpResponse servletServerResponse = (ServletServerHttpResponse)response;
            this.asyncRequestControl = new ServletServerHttpAsyncRequestControl(this, servletServerResponse);
        }
        return this.asyncRequestControl;
    }
}
