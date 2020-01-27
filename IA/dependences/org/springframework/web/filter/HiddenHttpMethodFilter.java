// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.filter;

import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.Locale;
import org.springframework.util.StringUtils;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.util.Assert;

public class HiddenHttpMethodFilter extends OncePerRequestFilter
{
    public static final String DEFAULT_METHOD_PARAM = "_method";
    private String methodParam;
    
    public HiddenHttpMethodFilter() {
        this.methodParam = "_method";
    }
    
    public void setMethodParam(final String methodParam) {
        Assert.hasText(methodParam, "'methodParam' must not be empty");
        this.methodParam = methodParam;
    }
    
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        final String paramValue = request.getParameter(this.methodParam);
        if ("POST".equals(request.getMethod()) && StringUtils.hasLength(paramValue)) {
            final String method = paramValue.toUpperCase(Locale.ENGLISH);
            final HttpServletRequest wrapper = (HttpServletRequest)new HttpMethodRequestWrapper(request, method);
            filterChain.doFilter((ServletRequest)wrapper, (ServletResponse)response);
        }
        else {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
    }
    
    private static class HttpMethodRequestWrapper extends HttpServletRequestWrapper
    {
        private final String method;
        
        public HttpMethodRequestWrapper(final HttpServletRequest request, final String method) {
            super(request);
            this.method = method;
        }
        
        public String getMethod() {
            return this.method;
        }
    }
}
