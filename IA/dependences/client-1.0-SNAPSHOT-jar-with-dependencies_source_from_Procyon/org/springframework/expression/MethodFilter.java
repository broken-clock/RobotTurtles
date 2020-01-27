// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression;

import java.lang.reflect.Method;
import java.util.List;

public interface MethodFilter
{
    List<Method> filter(final List<Method> p0);
}
