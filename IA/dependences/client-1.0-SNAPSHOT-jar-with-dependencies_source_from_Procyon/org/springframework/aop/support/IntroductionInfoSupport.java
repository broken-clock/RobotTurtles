// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import java.io.IOException;
import java.io.ObjectInputStream;
import org.aopalliance.intercept.MethodInvocation;
import java.util.Collection;
import org.springframework.util.ClassUtils;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.io.Serializable;
import org.springframework.aop.IntroductionInfo;

public class IntroductionInfoSupport implements IntroductionInfo, Serializable
{
    protected final Set<Class<?>> publishedInterfaces;
    private transient Map<Method, Boolean> rememberedMethods;
    
    public IntroductionInfoSupport() {
        this.publishedInterfaces = new HashSet<Class<?>>();
        this.rememberedMethods = new ConcurrentHashMap<Method, Boolean>(32);
    }
    
    public void suppressInterface(final Class<?> intf) {
        this.publishedInterfaces.remove(intf);
    }
    
    @Override
    public Class<?>[] getInterfaces() {
        return this.publishedInterfaces.toArray(new Class[this.publishedInterfaces.size()]);
    }
    
    public boolean implementsInterface(final Class<?> ifc) {
        for (final Class<?> pubIfc : this.publishedInterfaces) {
            if (ifc.isInterface() && ifc.isAssignableFrom(pubIfc)) {
                return true;
            }
        }
        return false;
    }
    
    protected void implementInterfacesOnObject(final Object delegate) {
        this.publishedInterfaces.addAll(ClassUtils.getAllInterfacesAsSet(delegate));
    }
    
    protected final boolean isMethodOnIntroducedInterface(final MethodInvocation mi) {
        final Boolean rememberedResult = this.rememberedMethods.get(mi.getMethod());
        if (rememberedResult != null) {
            return rememberedResult;
        }
        final boolean result = this.implementsInterface(mi.getMethod().getDeclaringClass());
        this.rememberedMethods.put(mi.getMethod(), result);
        return result;
    }
    
    private void readObject(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.rememberedMethods = new ConcurrentHashMap<Method, Boolean>(32);
    }
}
