// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import org.springframework.aop.ProxyMethodInvocation;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.DynamicIntroductionAdvice;
import java.util.WeakHashMap;
import java.util.Map;
import org.springframework.aop.IntroductionInterceptor;

public class DelegatePerTargetObjectIntroductionInterceptor extends IntroductionInfoSupport implements IntroductionInterceptor
{
    private final Map<Object, Object> delegateMap;
    private Class<?> defaultImplType;
    private Class<?> interfaceType;
    
    public DelegatePerTargetObjectIntroductionInterceptor(final Class<?> defaultImplType, final Class<?> interfaceType) {
        this.delegateMap = new WeakHashMap<Object, Object>();
        this.defaultImplType = defaultImplType;
        this.interfaceType = interfaceType;
        final Object delegate = this.createNewDelegate();
        this.implementInterfacesOnObject(delegate);
        this.suppressInterface(IntroductionInterceptor.class);
        this.suppressInterface(DynamicIntroductionAdvice.class);
    }
    
    @Override
    public Object invoke(final MethodInvocation mi) throws Throwable {
        if (this.isMethodOnIntroducedInterface(mi)) {
            final Object delegate = this.getIntroductionDelegateFor(mi.getThis());
            Object retVal = AopUtils.invokeJoinpointUsingReflection(delegate, mi.getMethod(), mi.getArguments());
            if (retVal == delegate && mi instanceof ProxyMethodInvocation) {
                retVal = ((ProxyMethodInvocation)mi).getProxy();
            }
            return retVal;
        }
        return this.doProceed(mi);
    }
    
    protected Object doProceed(final MethodInvocation mi) throws Throwable {
        return mi.proceed();
    }
    
    private Object getIntroductionDelegateFor(final Object targetObject) {
        synchronized (this.delegateMap) {
            if (this.delegateMap.containsKey(targetObject)) {
                return this.delegateMap.get(targetObject);
            }
            final Object delegate = this.createNewDelegate();
            this.delegateMap.put(targetObject, delegate);
            return delegate;
        }
    }
    
    private Object createNewDelegate() {
        try {
            return this.defaultImplType.newInstance();
        }
        catch (Throwable ex) {
            throw new IllegalArgumentException("Cannot create default implementation for '" + this.interfaceType.getName() + "' mixin (" + this.defaultImplType.getName() + "): " + ex);
        }
    }
}
