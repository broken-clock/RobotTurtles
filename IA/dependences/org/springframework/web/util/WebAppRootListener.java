// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class WebAppRootListener implements ServletContextListener
{
    public void contextInitialized(final ServletContextEvent event) {
        WebUtils.setWebAppRootSystemProperty(event.getServletContext());
    }
    
    public void contextDestroyed(final ServletContextEvent event) {
        WebUtils.removeWebAppRootSystemProperty(event.getServletContext());
    }
}
