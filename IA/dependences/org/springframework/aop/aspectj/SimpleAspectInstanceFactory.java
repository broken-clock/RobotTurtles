// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.aspectj;

import org.springframework.aop.framework.AopConfigException;
import org.springframework.util.Assert;

public class SimpleAspectInstanceFactory implements AspectInstanceFactory
{
    private final Class<?> aspectClass;
    
    public SimpleAspectInstanceFactory(final Class<?> aspectClass) {
        Assert.notNull(aspectClass, "Aspect class must not be null");
        this.aspectClass = aspectClass;
    }
    
    public final Class<?> getAspectClass() {
        return this.aspectClass;
    }
    
    @Override
    public final Object getAspectInstance() {
        try {
            return this.aspectClass.newInstance();
        }
        catch (InstantiationException ex) {
            throw new AopConfigException("Unable to instantiate aspect class [" + this.aspectClass.getName() + "]", ex);
        }
        catch (IllegalAccessException ex2) {
            throw new AopConfigException("Cannot access element class [" + this.aspectClass.getName() + "]", ex2);
        }
    }
    
    @Override
    public ClassLoader getAspectClassLoader() {
        return this.aspectClass.getClassLoader();
    }
    
    @Override
    public int getOrder() {
        return this.getOrderForAspectClass(this.aspectClass);
    }
    
    protected int getOrderForAspectClass(final Class<?> aspectClass) {
        return Integer.MAX_VALUE;
    }
}
