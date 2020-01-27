// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.ui.context;

public interface HierarchicalThemeSource extends ThemeSource
{
    void setParentThemeSource(final ThemeSource p0);
    
    ThemeSource getParentThemeSource();
}
