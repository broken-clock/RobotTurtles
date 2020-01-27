// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.filter;

import org.springframework.web.context.request.async.WebAsyncUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;

public abstract class OncePerRequestFilter extends GenericFilterBean
{
    public static final String ALREADY_FILTERED_SUFFIX = ".FILTERED";
    
    public final void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException("OncePerRequestFilter just supports HTTP requests");
        }
        final HttpServletRequest httpRequest = (HttpServletRequest)request;
        final HttpServletResponse httpResponse = (HttpServletResponse)response;
        final String alreadyFilteredAttributeName = this.getAlreadyFilteredAttributeName();
        final boolean hasAlreadyFilteredAttribute = request.getAttribute(alreadyFilteredAttributeName) != null;
        if (hasAlreadyFilteredAttribute || this.skipDispatch(httpRequest) || this.shouldNotFilter(httpRequest)) {
            filterChain.doFilter(request, response);
        }
        else {
            request.setAttribute(alreadyFilteredAttributeName, (Object)Boolean.TRUE);
            try {
                this.doFilterInternal(httpRequest, httpResponse, filterChain);
            }
            finally {
                request.removeAttribute(alreadyFilteredAttributeName);
            }
        }
    }
    
    private boolean skipDispatch(final HttpServletRequest request) {
        return (this.isAsyncDispatch(request) && this.shouldNotFilterAsyncDispatch()) || (request.getAttribute("javax.servlet.error.request_uri") != null && this.shouldNotFilterErrorDispatch());
    }
    
    protected boolean isAsyncDispatch(final HttpServletRequest request) {
        return WebAsyncUtils.getAsyncManager((ServletRequest)request).hasConcurrentResult();
    }
    
    protected boolean isAsyncStarted(final HttpServletRequest request) {
        return WebAsyncUtils.getAsyncManager((ServletRequest)request).isConcurrentHandlingStarted();
    }
    
    protected String getAlreadyFilteredAttributeName() {
        String name = this.getFilterName();
        if (name == null) {
            name = this.getClass().getName();
        }
        return name + ".FILTERED";
    }
    
    protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
        return false;
    }
    
    protected boolean shouldNotFilterAsyncDispatch() {
        return true;
    }
    
    protected boolean shouldNotFilterErrorDispatch() {
        return true;
    }
    
    protected abstract void doFilterInternal(final HttpServletRequest p0, final HttpServletResponse p1, final FilterChain p2) throws ServletException, IOException;
}
