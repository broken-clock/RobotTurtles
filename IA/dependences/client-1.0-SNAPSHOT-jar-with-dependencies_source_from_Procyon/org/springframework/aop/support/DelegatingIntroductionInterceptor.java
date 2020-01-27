// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import org.springframework.aop.ProxyMethodInvocation;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.DynamicIntroductionAdvice;
import org.springframework.util.Assert;
import org.springframework.aop.IntroductionInterceptor;

public class DelegatingIntroductionInterceptor extends IntroductionInfoSupport implements IntroductionInterceptor
{
    private Object delegate;
    
    public DelegatingIntroductionInterceptor(final Object delegate) {
        this.init(delegate);
    }
    
    protected DelegatingIntroductionInterceptor() {
        this.init(this);
    }
    
    private void init(final Object delegate) {
        Assert.notNull(delegate, "Delegate must not be null");
        this.implementInterfacesOnObject(this.delegate = delegate);
        this.suppressInterface(IntroductionInterceptor.class);
        this.suppressInterface(DynamicIntroductionAdvice.class);
    }
    
    @Override
    public Object invoke(final MethodInvocation mi) throws Throwable {
        if (this.isMethodOnIntroducedInterface(mi)) {
            Object retVal = AopUtils.invokeJoinpointUsingReflection(this.delegate, mi.getMethod(), mi.getArguments());
            if (retVal == this.delegate && mi instanceof ProxyMethodInvocation) {
                final Object proxy = ((ProxyMethodInvocation)mi).getProxy();
                if (mi.getMethod().getReturnType().isInstance(proxy)) {
                    retVal = proxy;
                }
            }
            return retVal;
        }
        return this.doProceed(mi);
    }
    
    protected Object doProceed(final MethodInvocation mi) throws Throwable {
        return mi.proceed();
    }
}
