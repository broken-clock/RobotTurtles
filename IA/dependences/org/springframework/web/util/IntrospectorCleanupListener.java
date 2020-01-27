// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.util;

import java.beans.Introspector;
import org.springframework.beans.CachedIntrospectionResults;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class IntrospectorCleanupListener implements ServletContextListener
{
    public void contextInitialized(final ServletContextEvent event) {
        CachedIntrospectionResults.acceptClassLoader(Thread.currentThread().getContextClassLoader());
    }
    
    public void contextDestroyed(final ServletContextEvent event) {
        CachedIntrospectionResults.clearClassLoader(Thread.currentThread().getContextClassLoader());
        Introspector.flushCaches();
    }
}
