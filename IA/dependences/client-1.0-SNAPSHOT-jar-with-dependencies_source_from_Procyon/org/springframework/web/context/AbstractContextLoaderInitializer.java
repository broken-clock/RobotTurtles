// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context;

import java.util.EventListener;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.web.WebApplicationInitializer;

public abstract class AbstractContextLoaderInitializer implements WebApplicationInitializer
{
    protected final Log logger;
    
    public AbstractContextLoaderInitializer() {
        this.logger = LogFactory.getLog(this.getClass());
    }
    
    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {
        this.registerContextLoaderListener(servletContext);
    }
    
    protected void registerContextLoaderListener(final ServletContext servletContext) {
        final WebApplicationContext rootAppContext = this.createRootApplicationContext();
        if (rootAppContext != null) {
            servletContext.addListener((EventListener)new ContextLoaderListener(rootAppContext));
        }
        else {
            this.logger.debug("No ContextLoaderListener registered, as createRootApplicationContext() did not return an application context");
        }
    }
    
    protected abstract WebApplicationContext createRootApplicationContext();
}
