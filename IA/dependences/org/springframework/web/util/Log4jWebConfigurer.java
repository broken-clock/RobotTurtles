// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.util;

import java.io.FileNotFoundException;
import org.springframework.util.Log4jConfigurer;
import org.springframework.util.ResourceUtils;
import javax.servlet.ServletContext;

public abstract class Log4jWebConfigurer
{
    public static final String CONFIG_LOCATION_PARAM = "log4jConfigLocation";
    public static final String REFRESH_INTERVAL_PARAM = "log4jRefreshInterval";
    public static final String EXPOSE_WEB_APP_ROOT_PARAM = "log4jExposeWebAppRoot";
    
    public static void initLogging(final ServletContext servletContext) {
        if (exposeWebAppRoot(servletContext)) {
            WebUtils.setWebAppRootSystemProperty(servletContext);
        }
        String location = servletContext.getInitParameter("log4jConfigLocation");
        if (location != null) {
            try {
                location = ServletContextPropertyUtils.resolvePlaceholders(location, servletContext);
                if (!ResourceUtils.isUrl(location)) {
                    location = WebUtils.getRealPath(servletContext, location);
                }
                servletContext.log("Initializing log4j from [" + location + "]");
                final String intervalString = servletContext.getInitParameter("log4jRefreshInterval");
                if (intervalString != null) {
                    try {
                        final long refreshInterval = Long.parseLong(intervalString);
                        Log4jConfigurer.initLogging(location, refreshInterval);
                        return;
                    }
                    catch (NumberFormatException ex) {
                        throw new IllegalArgumentException("Invalid 'log4jRefreshInterval' parameter: " + ex.getMessage());
                    }
                }
                Log4jConfigurer.initLogging(location);
            }
            catch (FileNotFoundException ex2) {
                throw new IllegalArgumentException("Invalid 'log4jConfigLocation' parameter: " + ex2.getMessage());
            }
        }
    }
    
    public static void shutdownLogging(final ServletContext servletContext) {
        servletContext.log("Shutting down log4j");
        try {
            Log4jConfigurer.shutdownLogging();
        }
        finally {
            if (exposeWebAppRoot(servletContext)) {
                WebUtils.removeWebAppRootSystemProperty(servletContext);
            }
        }
    }
    
    private static boolean exposeWebAppRoot(final ServletContext servletContext) {
        final String exposeWebAppRootParam = servletContext.getInitParameter("log4jExposeWebAppRoot");
        return exposeWebAppRootParam == null || Boolean.valueOf(exposeWebAppRootParam);
    }
}
