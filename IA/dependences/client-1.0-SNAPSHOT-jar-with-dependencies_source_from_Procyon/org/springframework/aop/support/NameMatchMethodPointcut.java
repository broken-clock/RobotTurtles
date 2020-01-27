// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import org.springframework.util.ObjectUtils;
import org.springframework.util.PatternMatchUtils;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.io.Serializable;

public class NameMatchMethodPointcut extends StaticMethodMatcherPointcut implements Serializable
{
    private List<String> mappedNames;
    
    public NameMatchMethodPointcut() {
        this.mappedNames = new LinkedList<String>();
    }
    
    public void setMappedName(final String mappedName) {
        this.setMappedNames(new String[] { mappedName });
    }
    
    public void setMappedNames(final String[] mappedNames) {
        this.mappedNames = new LinkedList<String>();
        if (mappedNames != null) {
            this.mappedNames.addAll(Arrays.asList(mappedNames));
        }
    }
    
    public NameMatchMethodPointcut addMethodName(final String name) {
        this.mappedNames.add(name);
        return this;
    }
    
    @Override
    public boolean matches(final Method method, final Class<?> targetClass) {
        for (final String mappedName : this.mappedNames) {
            if (mappedName.equals(method.getName()) || this.isMatch(method.getName(), mappedName)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean isMatch(final String methodName, final String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, methodName);
    }
    
    @Override
    public boolean equals(final Object other) {
        return this == other || (other instanceof NameMatchMethodPointcut && ObjectUtils.nullSafeEquals(this.mappedNames, ((NameMatchMethodPointcut)other).mappedNames));
    }
    
    @Override
    public int hashCode() {
        return (this.mappedNames != null) ? this.mappedNames.hashCode() : 0;
    }
}
