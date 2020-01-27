// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.util;

import javax.servlet.ServletContext;
import org.springframework.util.PropertyPlaceholderHelper;

public abstract class ServletContextPropertyUtils
{
    private static final PropertyPlaceholderHelper strictHelper;
    private static final PropertyPlaceholderHelper nonStrictHelper;
    
    public static String resolvePlaceholders(final String text, final ServletContext servletContext) {
        return resolvePlaceholders(text, servletContext, false);
    }
    
    public static String resolvePlaceholders(final String text, final ServletContext servletContext, final boolean ignoreUnresolvablePlaceholders) {
        final PropertyPlaceholderHelper helper = ignoreUnresolvablePlaceholders ? ServletContextPropertyUtils.nonStrictHelper : ServletContextPropertyUtils.strictHelper;
        return helper.replacePlaceholders(text, new ServletContextPlaceholderResolver(text, servletContext));
    }
    
    static {
        strictHelper = new PropertyPlaceholderHelper("${", "}", ":", false);
        nonStrictHelper = new PropertyPlaceholderHelper("${", "}", ":", true);
    }
    
    private static class ServletContextPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver
    {
        private final String text;
        private final ServletContext servletContext;
        
        public ServletContextPlaceholderResolver(final String text, final ServletContext servletContext) {
            this.text = text;
            this.servletContext = servletContext;
        }
        
        @Override
        public String resolvePlaceholder(final String placeholderName) {
            try {
                String propVal = this.servletContext.getInitParameter(placeholderName);
                if (propVal == null) {
                    propVal = System.getProperty(placeholderName);
                    if (propVal == null) {
                        propVal = System.getenv(placeholderName);
                    }
                }
                return propVal;
            }
            catch (Throwable ex) {
                System.err.println("Could not resolve placeholder '" + placeholderName + "' in [" + this.text + "] as ServletContext init-parameter or system property: " + ex);
                return null;
            }
        }
    }
}
