// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import java.beans.PropertyEditor;

public interface PropertyEditorRegistry
{
    void registerCustomEditor(final Class<?> p0, final PropertyEditor p1);
    
    void registerCustomEditor(final Class<?> p0, final String p1, final PropertyEditor p2);
    
    PropertyEditor findCustomEditor(final Class<?> p0, final String p1);
}
