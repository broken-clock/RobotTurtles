// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.util.HashMap;
import org.apache.commons.logging.LogFactory;
import java.util.Set;
import java.util.HashSet;
import java.util.Properties;
import java.util.Map;
import org.apache.commons.logging.Log;

public class PropertyPlaceholderHelper
{
    private static final Log logger;
    private static final Map<String, String> wellKnownSimplePrefixes;
    private final String placeholderPrefix;
    private final String placeholderSuffix;
    private final String simplePrefix;
    private final String valueSeparator;
    private final boolean ignoreUnresolvablePlaceholders;
    
    public PropertyPlaceholderHelper(final String placeholderPrefix, final String placeholderSuffix) {
        this(placeholderPrefix, placeholderSuffix, null, true);
    }
    
    public PropertyPlaceholderHelper(final String placeholderPrefix, final String placeholderSuffix, final String valueSeparator, final boolean ignoreUnresolvablePlaceholders) {
        Assert.notNull(placeholderPrefix, "placeholderPrefix must not be null");
        Assert.notNull(placeholderSuffix, "placeholderSuffix must not be null");
        this.placeholderPrefix = placeholderPrefix;
        this.placeholderSuffix = placeholderSuffix;
        final String simplePrefixForSuffix = PropertyPlaceholderHelper.wellKnownSimplePrefixes.get(this.placeholderSuffix);
        if (simplePrefixForSuffix != null && this.placeholderPrefix.endsWith(simplePrefixForSuffix)) {
            this.simplePrefix = simplePrefixForSuffix;
        }
        else {
            this.simplePrefix = this.placeholderPrefix;
        }
        this.valueSeparator = valueSeparator;
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
    }
    
    public String replacePlaceholders(final String value, final Properties properties) {
        Assert.notNull(properties, "Argument 'properties' must not be null.");
        return this.replacePlaceholders(value, new PlaceholderResolver() {
            @Override
            public String resolvePlaceholder(final String placeholderName) {
                return properties.getProperty(placeholderName);
            }
        });
    }
    
    public String replacePlaceholders(final String value, final PlaceholderResolver placeholderResolver) {
        Assert.notNull(value, "Argument 'value' must not be null.");
        return this.parseStringValue(value, placeholderResolver, new HashSet<String>());
    }
    
    protected String parseStringValue(final String strVal, final PlaceholderResolver placeholderResolver, final Set<String> visitedPlaceholders) {
        final StringBuilder buf = new StringBuilder(strVal);
        int startIndex = strVal.indexOf(this.placeholderPrefix);
        while (startIndex != -1) {
            final int endIndex = this.findPlaceholderEndIndex(buf, startIndex);
            if (endIndex != -1) {
                final String originalPlaceholder;
                String placeholder = originalPlaceholder = buf.substring(startIndex + this.placeholderPrefix.length(), endIndex);
                if (!visitedPlaceholders.add(originalPlaceholder)) {
                    throw new IllegalArgumentException("Circular placeholder reference '" + originalPlaceholder + "' in property definitions");
                }
                placeholder = this.parseStringValue(placeholder, placeholderResolver, visitedPlaceholders);
                String propVal = placeholderResolver.resolvePlaceholder(placeholder);
                if (propVal == null && this.valueSeparator != null) {
                    final int separatorIndex = placeholder.indexOf(this.valueSeparator);
                    if (separatorIndex != -1) {
                        final String actualPlaceholder = placeholder.substring(0, separatorIndex);
                        final String defaultValue = placeholder.substring(separatorIndex + this.valueSeparator.length());
                        propVal = placeholderResolver.resolvePlaceholder(actualPlaceholder);
                        if (propVal == null) {
                            propVal = defaultValue;
                        }
                    }
                }
                if (propVal != null) {
                    propVal = this.parseStringValue(propVal, placeholderResolver, visitedPlaceholders);
                    buf.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal);
                    if (PropertyPlaceholderHelper.logger.isTraceEnabled()) {
                        PropertyPlaceholderHelper.logger.trace("Resolved placeholder '" + placeholder + "'");
                    }
                    startIndex = buf.indexOf(this.placeholderPrefix, startIndex + propVal.length());
                }
                else {
                    if (!this.ignoreUnresolvablePlaceholders) {
                        throw new IllegalArgumentException("Could not resolve placeholder '" + placeholder + "'" + " in string value \"" + strVal + "\"");
                    }
                    startIndex = buf.indexOf(this.placeholderPrefix, endIndex + this.placeholderSuffix.length());
                }
                visitedPlaceholders.remove(originalPlaceholder);
            }
            else {
                startIndex = -1;
            }
        }
        return buf.toString();
    }
    
    private int findPlaceholderEndIndex(final CharSequence buf, final int startIndex) {
        int index = startIndex + this.placeholderPrefix.length();
        int withinNestedPlaceholder = 0;
        while (index < buf.length()) {
            if (StringUtils.substringMatch(buf, index, this.placeholderSuffix)) {
                if (withinNestedPlaceholder <= 0) {
                    return index;
                }
                --withinNestedPlaceholder;
                index += this.placeholderSuffix.length();
            }
            else if (StringUtils.substringMatch(buf, index, this.simplePrefix)) {
                ++withinNestedPlaceholder;
                index += this.simplePrefix.length();
            }
            else {
                ++index;
            }
        }
        return -1;
    }
    
    static {
        logger = LogFactory.getLog(PropertyPlaceholderHelper.class);
        (wellKnownSimplePrefixes = new HashMap<String, String>(4)).put("}", "{");
        PropertyPlaceholderHelper.wellKnownSimplePrefixes.put("]", "[");
        PropertyPlaceholderHelper.wellKnownSimplePrefixes.put(")", "(");
    }
    
    public interface PlaceholderResolver
    {
        String resolvePlaceholder(final String p0);
    }
}
