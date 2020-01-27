// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

public abstract class PatternMatchUtils
{
    public static boolean simpleMatch(final String pattern, final String str) {
        if (pattern == null || str == null) {
            return false;
        }
        final int firstIndex = pattern.indexOf(42);
        if (firstIndex == -1) {
            return pattern.equals(str);
        }
        if (firstIndex != 0) {
            return str.length() >= firstIndex && pattern.substring(0, firstIndex).equals(str.substring(0, firstIndex)) && simpleMatch(pattern.substring(firstIndex), str.substring(firstIndex));
        }
        if (pattern.length() == 1) {
            return true;
        }
        final int nextIndex = pattern.indexOf(42, firstIndex + 1);
        if (nextIndex == -1) {
            return str.endsWith(pattern.substring(1));
        }
        final String part = pattern.substring(1, nextIndex);
        for (int partIndex = str.indexOf(part); partIndex != -1; partIndex = str.indexOf(part, partIndex + 1)) {
            if (simpleMatch(pattern.substring(nextIndex), str.substring(partIndex + part.length()))) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean simpleMatch(final String[] patterns, final String str) {
        if (patterns != null) {
            for (int i = 0; i < patterns.length; ++i) {
                if (simpleMatch(patterns[i], str)) {
                    return true;
                }
            }
        }
        return false;
    }
}
