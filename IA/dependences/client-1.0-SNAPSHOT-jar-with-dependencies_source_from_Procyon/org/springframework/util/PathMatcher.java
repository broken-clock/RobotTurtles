// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.util.Comparator;
import java.util.Map;

public interface PathMatcher
{
    boolean isPattern(final String p0);
    
    boolean match(final String p0, final String p1);
    
    boolean matchStart(final String p0, final String p1);
    
    String extractPathWithinPattern(final String p0, final String p1);
    
    Map<String, String> extractUriTemplateVariables(final String p0, final String p1);
    
    Comparator<String> getPatternComparator(final String p0);
    
    String combine(final String p0, final String p1);
}
