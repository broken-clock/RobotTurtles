// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public interface ParameterNameDiscoverer
{
    String[] getParameterNames(final Method p0);
    
    String[] getParameterNames(final Constructor<?> p0);
}
