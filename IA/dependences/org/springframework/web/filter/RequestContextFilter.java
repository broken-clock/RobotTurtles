// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.filter;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.context.i18n.LocaleContextHolder;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class RequestContextFilter extends OncePerRequestFilter
{
    private boolean threadContextInheritable;
    
    public RequestContextFilter() {
        this.threadContextInheritable = false;
    }
    
    public void setThreadContextInheritable(final boolean threadContextInheritable) {
        this.threadContextInheritable = threadContextInheritable;
    }
    
    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }
    
    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }
    
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        final ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        this.initContextHolders(request, attributes);
        try {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
        finally {
            this.resetContextHolders();
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Cleared thread-bound request context: " + request);
            }
            attributes.requestCompleted();
        }
    }
    
    private void initContextHolders(final HttpServletRequest request, final ServletRequestAttributes requestAttributes) {
        LocaleContextHolder.setLocale(request.getLocale(), this.threadContextInheritable);
        RequestContextHolder.setRequestAttributes(requestAttributes, this.threadContextInheritable);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Bound request context to thread: " + request);
        }
    }
    
    private void resetContextHolders() {
        LocaleContextHolder.resetLocaleContext();
        RequestContextHolder.resetRequestAttributes();
    }
}
