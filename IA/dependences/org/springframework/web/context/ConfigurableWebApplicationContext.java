// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.springframework.context.ConfigurableApplicationContext;

public interface ConfigurableWebApplicationContext extends WebApplicationContext, ConfigurableApplicationContext
{
    public static final String APPLICATION_CONTEXT_ID_PREFIX = WebApplicationContext.class.getName() + ":";
    public static final String SERVLET_CONFIG_BEAN_NAME = "servletConfig";
    
    void setServletContext(final ServletContext p0);
    
    void setServletConfig(final ServletConfig p0);
    
    ServletConfig getServletConfig();
    
    void setNamespace(final String p0);
    
    String getNamespace();
    
    void setConfigLocation(final String p0);
    
    void setConfigLocations(final String[] p0);
    
    String[] getConfigLocations();
}
