// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContextLoaderListener extends ContextLoader implements ServletContextListener
{
    public ContextLoaderListener() {
    }
    
    public ContextLoaderListener(final WebApplicationContext context) {
        super(context);
    }
    
    public void contextInitialized(final ServletContextEvent event) {
        this.initWebApplicationContext(event.getServletContext());
    }
    
    public void contextDestroyed(final ServletContextEvent event) {
        this.closeWebApplicationContext(event.getServletContext());
        ContextCleanupListener.cleanupAttributes(event.getServletContext());
    }
}
