// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.event;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import java.lang.reflect.Constructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisherAware;
import org.aopalliance.intercept.MethodInterceptor;

public class EventPublicationInterceptor implements MethodInterceptor, ApplicationEventPublisherAware, InitializingBean
{
    private Constructor<?> applicationEventClassConstructor;
    private ApplicationEventPublisher applicationEventPublisher;
    
    public void setApplicationEventClass(final Class<?> applicationEventClass) {
        if (ApplicationEvent.class.equals(applicationEventClass) || !ApplicationEvent.class.isAssignableFrom(applicationEventClass)) {
            throw new IllegalArgumentException("applicationEventClass needs to extend ApplicationEvent");
        }
        try {
            this.applicationEventClassConstructor = applicationEventClass.getConstructor(Object.class);
        }
        catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException("applicationEventClass [" + applicationEventClass.getName() + "] does not have the required Object constructor: " + ex);
        }
    }
    
    @Override
    public void setApplicationEventPublisher(final ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.applicationEventClassConstructor == null) {
            throw new IllegalArgumentException("applicationEventClass is required");
        }
    }
    
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        final Object retVal = invocation.proceed();
        final ApplicationEvent event = (ApplicationEvent)this.applicationEventClassConstructor.newInstance(invocation.getThis());
        this.applicationEventPublisher.publishEvent(event);
        return retVal;
    }
}
