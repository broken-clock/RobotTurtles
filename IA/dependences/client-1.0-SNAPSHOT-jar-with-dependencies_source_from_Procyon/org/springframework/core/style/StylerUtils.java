// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.style;

public abstract class StylerUtils
{
    static final ValueStyler DEFAULT_VALUE_STYLER;
    
    public static String style(final Object value) {
        return StylerUtils.DEFAULT_VALUE_STYLER.style(value);
    }
    
    static {
        DEFAULT_VALUE_STYLER = new DefaultValueStyler();
    }
}
