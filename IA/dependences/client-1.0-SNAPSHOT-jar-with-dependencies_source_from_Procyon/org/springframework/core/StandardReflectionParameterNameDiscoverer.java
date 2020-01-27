// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.Method;

public class StandardReflectionParameterNameDiscoverer implements ParameterNameDiscoverer
{
    @Override
    public String[] getParameterNames(final Method method) {
        final Parameter[] parameters = method.getParameters();
        final String[] parameterNames = new String[parameters.length];
        for (int i = 0; i < parameters.length; ++i) {
            final Parameter param = parameters[i];
            if (!param.isNamePresent()) {
                return null;
            }
            parameterNames[i] = param.getName();
        }
        return parameterNames;
    }
    
    @Override
    public String[] getParameterNames(final Constructor<?> ctor) {
        final Parameter[] parameters = ctor.getParameters();
        final String[] parameterNames = new String[parameters.length];
        for (int i = 0; i < parameters.length; ++i) {
            final Parameter param = parameters[i];
            if (!param.isNamePresent()) {
                return null;
            }
            parameterNames[i] = param.getName();
        }
        return parameterNames;
    }
}
