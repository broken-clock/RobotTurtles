// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.multipart.support;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.filter.OncePerRequestFilter;

public class MultipartFilter extends OncePerRequestFilter
{
    public static final String DEFAULT_MULTIPART_RESOLVER_BEAN_NAME = "filterMultipartResolver";
    private final MultipartResolver defaultMultipartResolver;
    private String multipartResolverBeanName;
    
    public MultipartFilter() {
        this.defaultMultipartResolver = new StandardServletMultipartResolver();
        this.multipartResolverBeanName = "filterMultipartResolver";
    }
    
    public void setMultipartResolverBeanName(final String multipartResolverBeanName) {
        this.multipartResolverBeanName = multipartResolverBeanName;
    }
    
    protected String getMultipartResolverBeanName() {
        return this.multipartResolverBeanName;
    }
    
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        final MultipartResolver multipartResolver = this.lookupMultipartResolver(request);
        HttpServletRequest processedRequest = request;
        if (multipartResolver.isMultipart(processedRequest)) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Resolving multipart request [" + processedRequest.getRequestURI() + "] with MultipartFilter");
            }
            processedRequest = (HttpServletRequest)multipartResolver.resolveMultipart(processedRequest);
        }
        else if (this.logger.isDebugEnabled()) {
            this.logger.debug("Request [" + processedRequest.getRequestURI() + "] is not a multipart request");
        }
        try {
            filterChain.doFilter((ServletRequest)processedRequest, (ServletResponse)response);
        }
        finally {
            if (processedRequest instanceof MultipartHttpServletRequest) {
                multipartResolver.cleanupMultipart((MultipartHttpServletRequest)processedRequest);
            }
        }
    }
    
    protected MultipartResolver lookupMultipartResolver(final HttpServletRequest request) {
        return this.lookupMultipartResolver();
    }
    
    protected MultipartResolver lookupMultipartResolver() {
        final WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        final String beanName = this.getMultipartResolverBeanName();
        if (wac != null && wac.containsBean(beanName)) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using MultipartResolver '" + beanName + "' for MultipartFilter");
            }
            return wac.getBean(beanName, MultipartResolver.class);
        }
        return this.defaultMultipartResolver;
    }
}
