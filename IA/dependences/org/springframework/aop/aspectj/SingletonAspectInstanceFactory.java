// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.aspectj;

import org.springframework.core.Ordered;
import org.springframework.util.Assert;

public class SingletonAspectInstanceFactory implements AspectInstanceFactory
{
    private final Object aspectInstance;
    
    public SingletonAspectInstanceFactory(final Object aspectInstance) {
        Assert.notNull(aspectInstance, "Aspect instance must not be null");
        this.aspectInstance = aspectInstance;
    }
    
    @Override
    public final Object getAspectInstance() {
        return this.aspectInstance;
    }
    
    @Override
    public ClassLoader getAspectClassLoader() {
        return this.aspectInstance.getClass().getClassLoader();
    }
    
    @Override
    public int getOrder() {
        if (this.aspectInstance instanceof Ordered) {
            return ((Ordered)this.aspectInstance).getOrder();
        }
        return this.getOrderForAspectClass(this.aspectInstance.getClass());
    }
    
    protected int getOrderForAspectClass(final Class<?> aspectClass) {
        return Integer.MAX_VALUE;
    }
}
