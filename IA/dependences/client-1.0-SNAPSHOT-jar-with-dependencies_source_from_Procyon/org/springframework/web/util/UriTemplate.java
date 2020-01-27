// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import org.springframework.util.Assert;
import java.util.regex.Matcher;
import java.net.URI;
import java.util.Map;
import java.util.List;
import java.util.regex.Pattern;
import java.io.Serializable;

public class UriTemplate implements Serializable
{
    private static final Pattern NAMES_PATTERN;
    private static final String DEFAULT_VARIABLE_PATTERN = "(.*)";
    private final UriComponents uriComponents;
    private final List<String> variableNames;
    private final Pattern matchPattern;
    private final String uriTemplate;
    
    public UriTemplate(final String uriTemplate) {
        final Parser parser = new Parser(uriTemplate);
        this.uriTemplate = uriTemplate;
        this.variableNames = parser.getVariableNames();
        this.matchPattern = parser.getMatchPattern();
        this.uriComponents = UriComponentsBuilder.fromUriString(uriTemplate).build();
    }
    
    public List<String> getVariableNames() {
        return this.variableNames;
    }
    
    public URI expand(final Map<String, ?> uriVariables) {
        final UriComponents expandedComponents = this.uriComponents.expand(uriVariables);
        final UriComponents encodedComponents = expandedComponents.encode();
        return encodedComponents.toUri();
    }
    
    public URI expand(final Object... uriVariableValues) {
        final UriComponents expandedComponents = this.uriComponents.expand(uriVariableValues);
        final UriComponents encodedComponents = expandedComponents.encode();
        return encodedComponents.toUri();
    }
    
    public boolean matches(final String uri) {
        if (uri == null) {
            return false;
        }
        final Matcher matcher = this.matchPattern.matcher(uri);
        return matcher.matches();
    }
    
    public Map<String, String> match(final String uri) {
        Assert.notNull(uri, "'uri' must not be null");
        final Map<String, String> result = new LinkedHashMap<String, String>(this.variableNames.size());
        final Matcher matcher = this.matchPattern.matcher(uri);
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); ++i) {
                final String name = this.variableNames.get(i - 1);
                final String value = matcher.group(i);
                result.put(name, value);
            }
        }
        return result;
    }
    
    @Override
    public String toString() {
        return this.uriTemplate;
    }
    
    static {
        NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");
    }
    
    private static class Parser
    {
        private final List<String> variableNames;
        private final StringBuilder patternBuilder;
        
        private Parser(final String uriTemplate) {
            this.variableNames = new LinkedList<String>();
            this.patternBuilder = new StringBuilder();
            Assert.hasText(uriTemplate, "'uriTemplate' must not be null");
            final Matcher m = UriTemplate.NAMES_PATTERN.matcher(uriTemplate);
            int end = 0;
            while (m.find()) {
                this.patternBuilder.append(this.quote(uriTemplate, end, m.start()));
                final String match = m.group(1);
                final int colonIdx = match.indexOf(58);
                if (colonIdx == -1) {
                    this.patternBuilder.append("(.*)");
                    this.variableNames.add(match);
                }
                else {
                    if (colonIdx + 1 == match.length()) {
                        throw new IllegalArgumentException("No custom regular expression specified after ':' in \"" + match + "\"");
                    }
                    final String variablePattern = match.substring(colonIdx + 1, match.length());
                    this.patternBuilder.append('(');
                    this.patternBuilder.append(variablePattern);
                    this.patternBuilder.append(')');
                    final String variableName = match.substring(0, colonIdx);
                    this.variableNames.add(variableName);
                }
                end = m.end();
            }
            this.patternBuilder.append(this.quote(uriTemplate, end, uriTemplate.length()));
            final int lastIdx = this.patternBuilder.length() - 1;
            if (lastIdx >= 0 && this.patternBuilder.charAt(lastIdx) == '/') {
                this.patternBuilder.deleteCharAt(lastIdx);
            }
        }
        
        private String quote(final String fullPath, final int start, final int end) {
            if (start == end) {
                return "";
            }
            return Pattern.quote(fullPath.substring(start, end));
        }
        
        private List<String> getVariableNames() {
            return Collections.unmodifiableList((List<? extends String>)this.variableNames);
        }
        
        private Pattern getMatchPattern() {
            return Pattern.compile(this.patternBuilder.toString());
        }
    }
}
