// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class PrioritizedParameterNameDiscoverer implements ParameterNameDiscoverer
{
    private final List<ParameterNameDiscoverer> parameterNameDiscoverers;
    
    public PrioritizedParameterNameDiscoverer() {
        this.parameterNameDiscoverers = new LinkedList<ParameterNameDiscoverer>();
    }
    
    public void addDiscoverer(final ParameterNameDiscoverer pnd) {
        this.parameterNameDiscoverers.add(pnd);
    }
    
    @Override
    public String[] getParameterNames(final Method method) {
        for (final ParameterNameDiscoverer pnd : this.parameterNameDiscoverers) {
            final String[] result = pnd.getParameterNames(method);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
    
    @Override
    public String[] getParameterNames(final Constructor<?> ctor) {
        for (final ParameterNameDiscoverer pnd : this.parameterNameDiscoverers) {
            final String[] result = pnd.getParameterNames(ctor);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
