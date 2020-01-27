// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.filter;

import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.context.ConfigurableApplicationContext;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import org.springframework.util.Assert;
import javax.servlet.Filter;
import org.springframework.web.context.WebApplicationContext;

public class DelegatingFilterProxy extends GenericFilterBean
{
    private String contextAttribute;
    private WebApplicationContext webApplicationContext;
    private String targetBeanName;
    private boolean targetFilterLifecycle;
    private volatile Filter delegate;
    private final Object delegateMonitor;
    
    public DelegatingFilterProxy() {
        this.targetFilterLifecycle = false;
        this.delegateMonitor = new Object();
    }
    
    public DelegatingFilterProxy(final Filter delegate) {
        this.targetFilterLifecycle = false;
        this.delegateMonitor = new Object();
        Assert.notNull(delegate, "delegate Filter object must not be null");
        this.delegate = delegate;
    }
    
    public DelegatingFilterProxy(final String targetBeanName) {
        this(targetBeanName, null);
    }
    
    public DelegatingFilterProxy(final String targetBeanName, final WebApplicationContext wac) {
        this.targetFilterLifecycle = false;
        this.delegateMonitor = new Object();
        Assert.hasText(targetBeanName, "target Filter bean name must not be null or empty");
        this.setTargetBeanName(targetBeanName);
        this.webApplicationContext = wac;
        if (wac != null) {
            this.setEnvironment(wac.getEnvironment());
        }
    }
    
    public void setContextAttribute(final String contextAttribute) {
        this.contextAttribute = contextAttribute;
    }
    
    public String getContextAttribute() {
        return this.contextAttribute;
    }
    
    public void setTargetBeanName(final String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }
    
    protected String getTargetBeanName() {
        return this.targetBeanName;
    }
    
    public void setTargetFilterLifecycle(final boolean targetFilterLifecycle) {
        this.targetFilterLifecycle = targetFilterLifecycle;
    }
    
    protected boolean isTargetFilterLifecycle() {
        return this.targetFilterLifecycle;
    }
    
    @Override
    protected void initFilterBean() throws ServletException {
        synchronized (this.delegateMonitor) {
            if (this.delegate == null) {
                if (this.targetBeanName == null) {
                    this.targetBeanName = this.getFilterName();
                }
                final WebApplicationContext wac = this.findWebApplicationContext();
                if (wac != null) {
                    this.delegate = this.initDelegate(wac);
                }
            }
        }
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        Filter delegateToUse = this.delegate;
        if (delegateToUse == null) {
            synchronized (this.delegateMonitor) {
                if (this.delegate == null) {
                    final WebApplicationContext wac = this.findWebApplicationContext();
                    if (wac == null) {
                        throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
                    }
                    this.delegate = this.initDelegate(wac);
                }
                delegateToUse = this.delegate;
            }
        }
        this.invokeDelegate(delegateToUse, request, response, filterChain);
    }
    
    @Override
    public void destroy() {
        final Filter delegateToUse = this.delegate;
        if (delegateToUse != null) {
            this.destroyDelegate(delegateToUse);
        }
    }
    
    protected WebApplicationContext findWebApplicationContext() {
        if (this.webApplicationContext != null) {
            if (this.webApplicationContext instanceof ConfigurableApplicationContext && !((ConfigurableApplicationContext)this.webApplicationContext).isActive()) {
                ((ConfigurableApplicationContext)this.webApplicationContext).refresh();
            }
            return this.webApplicationContext;
        }
        final String attrName = this.getContextAttribute();
        if (attrName != null) {
            return WebApplicationContextUtils.getWebApplicationContext(this.getServletContext(), attrName);
        }
        return WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
    }
    
    protected Filter initDelegate(final WebApplicationContext wac) throws ServletException {
        final Filter delegate = wac.getBean(this.getTargetBeanName(), Filter.class);
        if (this.isTargetFilterLifecycle()) {
            delegate.init(this.getFilterConfig());
        }
        return delegate;
    }
    
    protected void invokeDelegate(final Filter delegate, final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        delegate.doFilter(request, response, filterChain);
    }
    
    protected void destroyDelegate(final Filter delegate) {
        if (this.isTargetFilterLifecycle()) {
            delegate.destroy();
        }
    }
}
