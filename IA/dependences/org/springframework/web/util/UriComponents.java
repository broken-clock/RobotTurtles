// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.net.URI;
import org.springframework.util.Assert;
import java.util.Map;
import java.io.UnsupportedEncodingException;
import org.springframework.util.MultiValueMap;
import java.util.List;
import java.util.regex.Pattern;
import java.io.Serializable;

public abstract class UriComponents implements Serializable
{
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final Pattern NAMES_PATTERN;
    private final String scheme;
    private final String fragment;
    
    protected UriComponents(final String scheme, final String fragment) {
        this.scheme = scheme;
        this.fragment = fragment;
    }
    
    public final String getScheme() {
        return this.scheme;
    }
    
    public abstract String getSchemeSpecificPart();
    
    public abstract String getUserInfo();
    
    public abstract String getHost();
    
    public abstract int getPort();
    
    public abstract String getPath();
    
    public abstract List<String> getPathSegments();
    
    public abstract String getQuery();
    
    public abstract MultiValueMap<String, String> getQueryParams();
    
    public final String getFragment() {
        return this.fragment;
    }
    
    public final UriComponents encode() {
        try {
            return this.encode("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new InternalError("\"UTF-8\" not supported");
        }
    }
    
    public abstract UriComponents encode(final String p0) throws UnsupportedEncodingException;
    
    public final UriComponents expand(final Map<String, ?> uriVariables) {
        Assert.notNull(uriVariables, "'uriVariables' must not be null");
        return this.expandInternal(new MapTemplateVariables(uriVariables));
    }
    
    public final UriComponents expand(final Object... uriVariableValues) {
        Assert.notNull(uriVariableValues, "'uriVariableValues' must not be null");
        return this.expandInternal(new VarArgsTemplateVariables(uriVariableValues));
    }
    
    public final UriComponents expand(final UriTemplateVariables uriTemplateVars) {
        Assert.notNull(uriTemplateVars, "'uriTemplateVars' must not be null");
        return this.expandInternal(uriTemplateVars);
    }
    
    abstract UriComponents expandInternal(final UriTemplateVariables p0);
    
    public abstract UriComponents normalize();
    
    public abstract String toUriString();
    
    public abstract URI toUri();
    
    @Override
    public final String toString() {
        return this.toUriString();
    }
    
    static String expandUriComponent(final String source, final UriTemplateVariables uriVariables) {
        if (source == null) {
            return null;
        }
        if (source.indexOf(123) == -1) {
            return source;
        }
        final Matcher matcher = UriComponents.NAMES_PATTERN.matcher(source);
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            final String match = matcher.group(1);
            final String variableName = getVariableName(match);
            final Object variableValue = uriVariables.getValue(variableName);
            if (UriTemplateVariables.SKIP_VALUE.equals(variableValue)) {
                continue;
            }
            final String variableValueString = getVariableValueAsString(variableValue);
            final String replacement = Matcher.quoteReplacement(variableValueString);
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    
    private static String getVariableName(final String match) {
        final int colonIdx = match.indexOf(58);
        return (colonIdx != -1) ? match.substring(0, colonIdx) : match;
    }
    
    private static String getVariableValueAsString(final Object variableValue) {
        return (variableValue != null) ? variableValue.toString() : "";
    }
    
    static {
        NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");
    }
    
    public interface UriTemplateVariables
    {
        public static final Object SKIP_VALUE = UriTemplateVariables.class;
        
        Object getValue(final String p0);
    }
    
    private static class MapTemplateVariables implements UriTemplateVariables
    {
        private final Map<String, ?> uriVariables;
        
        public MapTemplateVariables(final Map<String, ?> uriVariables) {
            this.uriVariables = uriVariables;
        }
        
        @Override
        public Object getValue(final String name) {
            if (!this.uriVariables.containsKey(name)) {
                throw new IllegalArgumentException("Map has no value for '" + name + "'");
            }
            return this.uriVariables.get(name);
        }
    }
    
    private static class VarArgsTemplateVariables implements UriTemplateVariables
    {
        private final Iterator<Object> valueIterator;
        
        public VarArgsTemplateVariables(final Object... uriVariableValues) {
            this.valueIterator = Arrays.asList(uriVariableValues).iterator();
        }
        
        @Override
        public Object getValue(final String name) {
            if (!this.valueIterator.hasNext()) {
                throw new IllegalArgumentException("Not enough variable values available to expand '" + name + "'");
            }
            return this.valueIterator.next();
        }
    }
}
