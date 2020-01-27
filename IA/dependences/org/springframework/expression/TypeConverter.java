// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression;

import org.springframework.core.convert.TypeDescriptor;

public interface TypeConverter
{
    boolean canConvert(final TypeDescriptor p0, final TypeDescriptor p1);
    
    Object convertValue(final Object p0, final TypeDescriptor p1, final TypeDescriptor p2);
}
