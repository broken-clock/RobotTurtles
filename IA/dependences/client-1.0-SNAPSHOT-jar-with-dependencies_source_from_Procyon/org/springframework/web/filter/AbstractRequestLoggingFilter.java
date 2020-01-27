// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.filter;

import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayOutputStream;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import org.springframework.util.StringUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.util.Assert;

public abstract class AbstractRequestLoggingFilter extends OncePerRequestFilter
{
    public static final String DEFAULT_BEFORE_MESSAGE_PREFIX = "Before request [";
    public static final String DEFAULT_BEFORE_MESSAGE_SUFFIX = "]";
    public static final String DEFAULT_AFTER_MESSAGE_PREFIX = "After request [";
    public static final String DEFAULT_AFTER_MESSAGE_SUFFIX = "]";
    private static final int DEFAULT_MAX_PAYLOAD_LENGTH = 50;
    private boolean includeQueryString;
    private boolean includeClientInfo;
    private boolean includePayload;
    private int maxPayloadLength;
    private String beforeMessagePrefix;
    private String beforeMessageSuffix;
    private String afterMessagePrefix;
    private String afterMessageSuffix;
    
    public AbstractRequestLoggingFilter() {
        this.includeQueryString = false;
        this.includeClientInfo = false;
        this.includePayload = false;
        this.maxPayloadLength = 50;
        this.beforeMessagePrefix = "Before request [";
        this.beforeMessageSuffix = "]";
        this.afterMessagePrefix = "After request [";
        this.afterMessageSuffix = "]";
    }
    
    public void setIncludeQueryString(final boolean includeQueryString) {
        this.includeQueryString = includeQueryString;
    }
    
    protected boolean isIncludeQueryString() {
        return this.includeQueryString;
    }
    
    public void setIncludeClientInfo(final boolean includeClientInfo) {
        this.includeClientInfo = includeClientInfo;
    }
    
    protected boolean isIncludeClientInfo() {
        return this.includeClientInfo;
    }
    
    public void setIncludePayload(final boolean includePayload) {
        this.includePayload = includePayload;
    }
    
    protected boolean isIncludePayload() {
        return this.includePayload;
    }
    
    public void setMaxPayloadLength(final int maxPayloadLength) {
        Assert.isTrue(maxPayloadLength >= 0, "'maxPayloadLength' should be larger than or equal to 0");
        this.maxPayloadLength = maxPayloadLength;
    }
    
    protected int getMaxPayloadLength() {
        return this.maxPayloadLength;
    }
    
    public void setBeforeMessagePrefix(final String beforeMessagePrefix) {
        this.beforeMessagePrefix = beforeMessagePrefix;
    }
    
    public void setBeforeMessageSuffix(final String beforeMessageSuffix) {
        this.beforeMessageSuffix = beforeMessageSuffix;
    }
    
    public void setAfterMessagePrefix(final String afterMessagePrefix) {
        this.afterMessagePrefix = afterMessagePrefix;
    }
    
    public void setAfterMessageSuffix(final String afterMessageSuffix) {
        this.afterMessageSuffix = afterMessageSuffix;
    }
    
    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        final boolean isFirstRequest = !this.isAsyncDispatch(request);
        if (this.isIncludePayload() && isFirstRequest) {
            request = (HttpServletRequest)new RequestCachingRequestWrapper(request);
        }
        if (isFirstRequest) {
            this.beforeRequest(request, this.getBeforeMessage(request));
        }
        try {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
        finally {
            if (!this.isAsyncStarted(request)) {
                this.afterRequest(request, this.getAfterMessage(request));
            }
        }
    }
    
    private String getBeforeMessage(final HttpServletRequest request) {
        return this.createMessage(request, this.beforeMessagePrefix, this.beforeMessageSuffix);
    }
    
    private String getAfterMessage(final HttpServletRequest request) {
        return this.createMessage(request, this.afterMessagePrefix, this.afterMessageSuffix);
    }
    
    protected String createMessage(final HttpServletRequest request, final String prefix, final String suffix) {
        final StringBuilder msg = new StringBuilder();
        msg.append(prefix);
        msg.append("uri=").append(request.getRequestURI());
        if (this.isIncludeQueryString()) {
            msg.append('?').append(request.getQueryString());
        }
        if (this.isIncludeClientInfo()) {
            final String client = request.getRemoteAddr();
            if (StringUtils.hasLength(client)) {
                msg.append(";client=").append(client);
            }
            final HttpSession session = request.getSession(false);
            if (session != null) {
                msg.append(";session=").append(session.getId());
            }
            final String user = request.getRemoteUser();
            if (user != null) {
                msg.append(";user=").append(user);
            }
        }
        if (this.isIncludePayload() && request instanceof RequestCachingRequestWrapper) {
            final RequestCachingRequestWrapper wrapper = (RequestCachingRequestWrapper)request;
            final byte[] buf = wrapper.toByteArray();
            if (buf.length > 0) {
                final int length = Math.min(buf.length, this.getMaxPayloadLength());
                String payload;
                try {
                    payload = new String(buf, 0, length, wrapper.getCharacterEncoding());
                }
                catch (UnsupportedEncodingException e) {
                    payload = "[unknown]";
                }
                msg.append(";payload=").append(payload);
            }
        }
        msg.append(suffix);
        return msg.toString();
    }
    
    protected abstract void beforeRequest(final HttpServletRequest p0, final String p1);
    
    protected abstract void afterRequest(final HttpServletRequest p0, final String p1);
    
    private static class RequestCachingRequestWrapper extends HttpServletRequestWrapper
    {
        private final ByteArrayOutputStream bos;
        private final ServletInputStream inputStream;
        private BufferedReader reader;
        
        private RequestCachingRequestWrapper(final HttpServletRequest request) throws IOException {
            super(request);
            this.bos = new ByteArrayOutputStream();
            this.inputStream = new RequestCachingInputStream(request.getInputStream());
        }
        
        public ServletInputStream getInputStream() throws IOException {
            return this.inputStream;
        }
        
        public String getCharacterEncoding() {
            return (super.getCharacterEncoding() != null) ? super.getCharacterEncoding() : "ISO-8859-1";
        }
        
        public BufferedReader getReader() throws IOException {
            if (this.reader == null) {
                this.reader = new BufferedReader(new InputStreamReader((InputStream)this.inputStream, this.getCharacterEncoding()));
            }
            return this.reader;
        }
        
        private byte[] toByteArray() {
            return this.bos.toByteArray();
        }
        
        private class RequestCachingInputStream extends ServletInputStream
        {
            private final ServletInputStream is;
            
            private RequestCachingInputStream(final ServletInputStream is) {
                this.is = is;
            }
            
            public int read() throws IOException {
                final int ch = this.is.read();
                if (ch != -1) {
                    RequestCachingRequestWrapper.this.bos.write(ch);
                }
                return ch;
            }
        }
    }
}
