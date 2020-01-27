// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert;

public interface ConversionService
{
    boolean canConvert(final Class<?> p0, final Class<?> p1);
    
    boolean canConvert(final TypeDescriptor p0, final TypeDescriptor p1);
    
     <T> T convert(final Object p0, final Class<T> p1);
    
    Object convert(final Object p0, final TypeDescriptor p1, final TypeDescriptor p2);
}
