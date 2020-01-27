// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.filter;

import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import javax.servlet.http.HttpServletResponseWrapper;
import org.springframework.util.DigestUtils;
import org.springframework.http.HttpMethod;
import java.io.OutputStream;
import org.springframework.util.StreamUtils;
import org.springframework.util.Assert;
import org.springframework.web.util.WebUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class ShallowEtagHeaderFilter extends OncePerRequestFilter
{
    private static final String HEADER_ETAG = "ETag";
    private static final String HEADER_IF_NONE_MATCH = "If-None-Match";
    private static final String HEADER_CACHE_CONTROL = "Cache-Control";
    private static final String DIRECTIVE_NO_STORE = "no-store";
    
    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }
    
    @Override
    protected void doFilterInternal(final HttpServletRequest request, HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        if (!this.isAsyncDispatch(request)) {
            response = (HttpServletResponse)new ShallowEtagResponseWrapper(response);
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        if (!this.isAsyncStarted(request)) {
            this.updateResponse(request, response);
        }
    }
    
    private void updateResponse(final HttpServletRequest request, HttpServletResponse response) throws IOException {
        final ShallowEtagResponseWrapper responseWrapper = WebUtils.getNativeResponse((ServletResponse)response, ShallowEtagResponseWrapper.class);
        Assert.notNull(responseWrapper, "ShallowEtagResponseWrapper not found");
        response = (HttpServletResponse)responseWrapper.getResponse();
        final byte[] body = responseWrapper.toByteArray();
        final int statusCode = responseWrapper.getStatusCode();
        if (this.isEligibleForEtag(request, (HttpServletResponse)responseWrapper, statusCode, body)) {
            final String responseETag = this.generateETagHeaderValue(body);
            response.setHeader("ETag", responseETag);
            final String requestETag = request.getHeader("If-None-Match");
            if (responseETag.equals(requestETag)) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("ETag [" + responseETag + "] equal to If-None-Match, sending 304");
                }
                response.setStatus(304);
            }
            else {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("ETag [" + responseETag + "] not equal to If-None-Match [" + requestETag + "], sending normal response");
                }
                this.copyBodyToResponse(body, response);
            }
        }
        else {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Response with status code [" + statusCode + "] not eligible for ETag");
            }
            this.copyBodyToResponse(body, response);
        }
    }
    
    private void copyBodyToResponse(final byte[] body, final HttpServletResponse response) throws IOException {
        if (body.length > 0) {
            response.setContentLength(body.length);
            StreamUtils.copy(body, (OutputStream)response.getOutputStream());
        }
    }
    
    protected boolean isEligibleForEtag(final HttpServletRequest request, final HttpServletResponse response, final int responseStatusCode, final byte[] responseBody) {
        return responseStatusCode >= 200 && responseStatusCode < 300 && HttpMethod.GET.name().equals(request.getMethod()) && (response.getHeader("Cache-Control") == null || !response.getHeader("Cache-Control").contains("no-store"));
    }
    
    protected String generateETagHeaderValue(final byte[] bytes) {
        final StringBuilder builder = new StringBuilder("\"0");
        DigestUtils.appendMd5DigestAsHex(bytes, builder);
        builder.append('\"');
        return builder.toString();
    }
    
    private static class ShallowEtagResponseWrapper extends HttpServletResponseWrapper
    {
        private final ByteArrayOutputStream content;
        private final ServletOutputStream outputStream;
        private PrintWriter writer;
        private int statusCode;
        
        public ShallowEtagResponseWrapper(final HttpServletResponse response) {
            super(response);
            this.content = new ByteArrayOutputStream();
            this.outputStream = new ResponseServletOutputStream();
            this.statusCode = 200;
        }
        
        public void setStatus(final int sc) {
            super.setStatus(sc);
            this.statusCode = sc;
        }
        
        public void setStatus(final int sc, final String sm) {
            super.setStatus(sc, sm);
            this.statusCode = sc;
        }
        
        public void sendError(final int sc) throws IOException {
            super.sendError(sc);
            this.statusCode = sc;
        }
        
        public void sendError(final int sc, final String msg) throws IOException {
            super.sendError(sc, msg);
            this.statusCode = sc;
        }
        
        public void setContentLength(final int len) {
        }
        
        public ServletOutputStream getOutputStream() {
            return this.outputStream;
        }
        
        public PrintWriter getWriter() throws IOException {
            if (this.writer == null) {
                final String characterEncoding = this.getCharacterEncoding();
                this.writer = ((characterEncoding != null) ? new ResponsePrintWriter(characterEncoding) : new ResponsePrintWriter("ISO-8859-1"));
            }
            return this.writer;
        }
        
        public void resetBuffer() {
            this.content.reset();
        }
        
        public void reset() {
            super.reset();
            this.resetBuffer();
        }
        
        private int getStatusCode() {
            return this.statusCode;
        }
        
        private byte[] toByteArray() {
            return this.content.toByteArray();
        }
        
        private class ResponseServletOutputStream extends ServletOutputStream
        {
            public void write(final int b) throws IOException {
                ShallowEtagResponseWrapper.this.content.write(b);
            }
            
            public void write(final byte[] b, final int off, final int len) throws IOException {
                ShallowEtagResponseWrapper.this.content.write(b, off, len);
            }
        }
        
        private class ResponsePrintWriter extends PrintWriter
        {
            public ResponsePrintWriter(final String characterEncoding) throws UnsupportedEncodingException {
                super(new OutputStreamWriter(ShallowEtagResponseWrapper.this.content, characterEncoding));
            }
            
            @Override
            public void write(final char[] buf, final int off, final int len) {
                super.write(buf, off, len);
                super.flush();
            }
            
            @Override
            public void write(final String s, final int off, final int len) {
                super.write(s, off, len);
                super.flush();
            }
            
            @Override
            public void write(final int c) {
                super.write(c);
                super.flush();
            }
        }
    }
}
