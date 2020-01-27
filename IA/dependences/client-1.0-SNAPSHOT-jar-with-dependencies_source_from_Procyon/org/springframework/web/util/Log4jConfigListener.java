// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Log4jConfigListener implements ServletContextListener
{
    public void contextInitialized(final ServletContextEvent event) {
        Log4jWebConfigurer.initLogging(event.getServletContext());
    }
    
    public void contextDestroyed(final ServletContextEvent event) {
        Log4jWebConfigurer.shutdownLogging(event.getServletContext());
    }
}
