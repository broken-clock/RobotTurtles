// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

public abstract class PropertyAccessorUtils
{
    public static String getPropertyName(final String propertyPath) {
        final int separatorIndex = propertyPath.endsWith("]") ? propertyPath.indexOf(91) : -1;
        return (separatorIndex != -1) ? propertyPath.substring(0, separatorIndex) : propertyPath;
    }
    
    public static boolean isNestedOrIndexedProperty(final String propertyPath) {
        if (propertyPath == null) {
            return false;
        }
        for (int i = 0; i < propertyPath.length(); ++i) {
            final char ch = propertyPath.charAt(i);
            if (ch == '.' || ch == '[') {
                return true;
            }
        }
        return false;
    }
    
    public static int getFirstNestedPropertySeparatorIndex(final String propertyPath) {
        return getNestedPropertySeparatorIndex(propertyPath, false);
    }
    
    public static int getLastNestedPropertySeparatorIndex(final String propertyPath) {
        return getNestedPropertySeparatorIndex(propertyPath, true);
    }
    
    private static int getNestedPropertySeparatorIndex(final String propertyPath, final boolean last) {
        boolean inKey = false;
        final int length = propertyPath.length();
        int i = last ? (length - 1) : 0;
        while (true) {
            if (last) {
                if (i < 0) {
                    break;
                }
            }
            else if (i >= length) {
                break;
            }
            switch (propertyPath.charAt(i)) {
                case '[':
                case ']': {
                    inKey = !inKey;
                    break;
                }
                case '.': {
                    if (!inKey) {
                        return i;
                    }
                    break;
                }
            }
            if (last) {
                --i;
            }
            else {
                ++i;
            }
        }
        return -1;
    }
    
    public static boolean matchesProperty(final String registeredPath, final String propertyPath) {
        return registeredPath.startsWith(propertyPath) && (registeredPath.length() == propertyPath.length() || (registeredPath.charAt(propertyPath.length()) == '[' && registeredPath.indexOf(93, propertyPath.length() + 1) == registeredPath.length() - 1));
    }
    
    public static String canonicalPropertyName(final String propertyName) {
        if (propertyName == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(propertyName);
        int searchIndex = 0;
        while (searchIndex != -1) {
            final int keyStart = sb.indexOf("[", searchIndex);
            searchIndex = -1;
            if (keyStart != -1) {
                int keyEnd = sb.indexOf("]", keyStart + "[".length());
                if (keyEnd == -1) {
                    continue;
                }
                final String key = sb.substring(keyStart + "[".length(), keyEnd);
                if ((key.startsWith("'") && key.endsWith("'")) || (key.startsWith("\"") && key.endsWith("\""))) {
                    sb.delete(keyStart + 1, keyStart + 2);
                    sb.delete(keyEnd - 2, keyEnd - 1);
                    keyEnd -= 2;
                }
                searchIndex = keyEnd + "]".length();
            }
        }
        return sb.toString();
    }
    
    public static String[] canonicalPropertyNames(final String[] propertyNames) {
        if (propertyNames == null) {
            return null;
        }
        final String[] result = new String[propertyNames.length];
        for (int i = 0; i < propertyNames.length; ++i) {
            result[i] = canonicalPropertyName(propertyNames[i]);
        }
        return result;
    }
}
