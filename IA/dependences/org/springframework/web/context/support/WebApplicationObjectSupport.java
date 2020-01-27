// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context.support;

import org.springframework.web.util.WebUtils;
import java.io.File;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.context.ApplicationContext;
import javax.servlet.ServletContext;
import org.springframework.web.context.ServletContextAware;
import org.springframework.context.support.ApplicationObjectSupport;

public abstract class WebApplicationObjectSupport extends ApplicationObjectSupport implements ServletContextAware
{
    private ServletContext servletContext;
    
    @Override
    public final void setServletContext(final ServletContext servletContext) {
        if (servletContext != this.servletContext && (this.servletContext = servletContext) != null) {
            this.initServletContext(servletContext);
        }
    }
    
    @Override
    protected boolean isContextRequired() {
        return true;
    }
    
    @Override
    protected void initApplicationContext(final ApplicationContext context) {
        super.initApplicationContext(context);
        if (this.servletContext == null && context instanceof WebApplicationContext) {
            this.servletContext = ((WebApplicationContext)context).getServletContext();
            if (this.servletContext != null) {
                this.initServletContext(this.servletContext);
            }
        }
    }
    
    protected void initServletContext(final ServletContext servletContext) {
    }
    
    protected final WebApplicationContext getWebApplicationContext() throws IllegalStateException {
        final ApplicationContext ctx = this.getApplicationContext();
        if (ctx instanceof WebApplicationContext) {
            return (WebApplicationContext)this.getApplicationContext();
        }
        if (this.isContextRequired()) {
            throw new IllegalStateException("WebApplicationObjectSupport instance [" + this + "] does not run in a WebApplicationContext but in: " + ctx);
        }
        return null;
    }
    
    protected final ServletContext getServletContext() throws IllegalStateException {
        if (this.servletContext != null) {
            return this.servletContext;
        }
        final ServletContext servletContext = this.getWebApplicationContext().getServletContext();
        if (servletContext == null && this.isContextRequired()) {
            throw new IllegalStateException("WebApplicationObjectSupport instance [" + this + "] does not run within a ServletContext. Make sure the object is fully configured!");
        }
        return servletContext;
    }
    
    protected final File getTempDir() throws IllegalStateException {
        return WebUtils.getTempDir(this.getServletContext());
    }
}
