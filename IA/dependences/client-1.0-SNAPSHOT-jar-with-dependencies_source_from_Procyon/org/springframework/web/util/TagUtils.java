// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.util;

import javax.servlet.jsp.tagext.Tag;
import org.springframework.util.Assert;

public abstract class TagUtils
{
    public static final String SCOPE_PAGE = "page";
    public static final String SCOPE_REQUEST = "request";
    public static final String SCOPE_SESSION = "session";
    public static final String SCOPE_APPLICATION = "application";
    
    public static int getScope(final String scope) {
        Assert.notNull(scope, "Scope to search for cannot be null");
        if (scope.equals("request")) {
            return 2;
        }
        if (scope.equals("session")) {
            return 3;
        }
        if (scope.equals("application")) {
            return 4;
        }
        return 1;
    }
    
    public static boolean hasAncestorOfType(final Tag tag, final Class<?> ancestorTagClass) {
        Assert.notNull(tag, "Tag cannot be null");
        Assert.notNull(ancestorTagClass, "Ancestor tag class cannot be null");
        if (!Tag.class.isAssignableFrom(ancestorTagClass)) {
            throw new IllegalArgumentException("Class '" + ancestorTagClass.getName() + "' is not a valid Tag type");
        }
        for (Tag ancestor = tag.getParent(); ancestor != null; ancestor = ancestor.getParent()) {
            if (ancestorTagClass.isAssignableFrom(ancestor.getClass())) {
                return true;
            }
        }
        return false;
    }
    
    public static void assertHasAncestorOfType(final Tag tag, final Class<?> ancestorTagClass, final String tagName, final String ancestorTagName) {
        Assert.hasText(tagName, "'tagName' must not be empty");
        Assert.hasText(ancestorTagName, "'ancestorTagName' must not be empty");
        if (!hasAncestorOfType(tag, ancestorTagClass)) {
            throw new IllegalStateException("The '" + tagName + "' tag can only be used inside a valid '" + ancestorTagName + "' tag.");
        }
    }
}
