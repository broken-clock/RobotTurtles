// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import java.lang.reflect.Field;
import org.springframework.core.MethodParameter;

public interface TypeConverter
{
     <T> T convertIfNecessary(final Object p0, final Class<T> p1) throws TypeMismatchException;
    
     <T> T convertIfNecessary(final Object p0, final Class<T> p1, final MethodParameter p2) throws TypeMismatchException;
    
     <T> T convertIfNecessary(final Object p0, final Class<T> p1, final Field p2) throws TypeMismatchException;
}
