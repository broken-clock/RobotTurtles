// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context;

import org.apache.commons.logging.LogFactory;
import java.util.Enumeration;
import org.springframework.beans.factory.DisposableBean;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import org.apache.commons.logging.Log;
import javax.servlet.ServletContextListener;

public class ContextCleanupListener implements ServletContextListener
{
    private static final Log logger;
    
    public void contextInitialized(final ServletContextEvent event) {
    }
    
    public void contextDestroyed(final ServletContextEvent event) {
        cleanupAttributes(event.getServletContext());
    }
    
    static void cleanupAttributes(final ServletContext sc) {
        final Enumeration<String> attrNames = (Enumeration<String>)sc.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            final String attrName = attrNames.nextElement();
            if (attrName.startsWith("org.springframework.")) {
                final Object attrValue = sc.getAttribute(attrName);
                if (!(attrValue instanceof DisposableBean)) {
                    continue;
                }
                try {
                    ((DisposableBean)attrValue).destroy();
                }
                catch (Throwable ex) {
                    ContextCleanupListener.logger.error("Couldn't invoke destroy method of attribute with name '" + attrName + "'", ex);
                }
            }
        }
    }
    
    static {
        logger = LogFactory.getLog(ContextCleanupListener.class);
    }
}
