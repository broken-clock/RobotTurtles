// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.event;

import org.springframework.core.Ordered;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.Assert;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class GenericApplicationListenerAdapter implements SmartApplicationListener
{
    private final ApplicationListener<ApplicationEvent> delegate;
    
    public GenericApplicationListenerAdapter(final ApplicationListener<?> delegate) {
        Assert.notNull(delegate, "Delegate listener must not be null");
        this.delegate = (ApplicationListener<ApplicationEvent>)delegate;
    }
    
    @Override
    public void onApplicationEvent(final ApplicationEvent event) {
        this.delegate.onApplicationEvent(event);
    }
    
    @Override
    public boolean supportsEventType(final Class<? extends ApplicationEvent> eventType) {
        Class<?> typeArg = GenericTypeResolver.resolveTypeArgument(this.delegate.getClass(), ApplicationListener.class);
        if (typeArg == null || typeArg.equals(ApplicationEvent.class)) {
            final Class<?> targetClass = AopUtils.getTargetClass(this.delegate);
            if (targetClass != this.delegate.getClass()) {
                typeArg = GenericTypeResolver.resolveTypeArgument(targetClass, ApplicationListener.class);
            }
        }
        return typeArg == null || typeArg.isAssignableFrom(eventType);
    }
    
    @Override
    public boolean supportsSourceType(final Class<?> sourceType) {
        return true;
    }
    
    @Override
    public int getOrder() {
        return (this.delegate instanceof Ordered) ? ((Ordered)this.delegate).getOrder() : Integer.MAX_VALUE;
    }
}
