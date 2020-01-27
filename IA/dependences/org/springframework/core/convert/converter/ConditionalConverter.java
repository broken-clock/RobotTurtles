// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.converter;

import org.springframework.core.convert.TypeDescriptor;

public interface ConditionalConverter
{
    boolean matches(final TypeDescriptor p0, final TypeDescriptor p1);
}
