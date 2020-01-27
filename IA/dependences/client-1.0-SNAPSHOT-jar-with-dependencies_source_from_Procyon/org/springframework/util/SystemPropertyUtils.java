// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

public abstract class SystemPropertyUtils
{
    public static final String PLACEHOLDER_PREFIX = "${";
    public static final String PLACEHOLDER_SUFFIX = "}";
    public static final String VALUE_SEPARATOR = ":";
    private static final PropertyPlaceholderHelper strictHelper;
    private static final PropertyPlaceholderHelper nonStrictHelper;
    
    public static String resolvePlaceholders(final String text) {
        return resolvePlaceholders(text, false);
    }
    
    public static String resolvePlaceholders(final String text, final boolean ignoreUnresolvablePlaceholders) {
        final PropertyPlaceholderHelper helper = ignoreUnresolvablePlaceholders ? SystemPropertyUtils.nonStrictHelper : SystemPropertyUtils.strictHelper;
        return helper.replacePlaceholders(text, new SystemPropertyPlaceholderResolver(text));
    }
    
    static {
        strictHelper = new PropertyPlaceholderHelper("${", "}", ":", false);
        nonStrictHelper = new PropertyPlaceholderHelper("${", "}", ":", true);
    }
    
    private static class SystemPropertyPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver
    {
        private final String text;
        
        public SystemPropertyPlaceholderResolver(final String text) {
            this.text = text;
        }
        
        @Override
        public String resolvePlaceholder(final String placeholderName) {
            try {
                String propVal = System.getProperty(placeholderName);
                if (propVal == null) {
                    propVal = System.getenv(placeholderName);
                }
                return propVal;
            }
            catch (Throwable ex) {
                System.err.println("Could not resolve placeholder '" + placeholderName + "' in [" + this.text + "] as system property: " + ex);
                return null;
            }
        }
    }
}
