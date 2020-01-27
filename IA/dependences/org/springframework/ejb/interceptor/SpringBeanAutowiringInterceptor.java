// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.ejb.interceptor;

import javax.ejb.PrePassivate;
import javax.annotation.PreDestroy;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import javax.ejb.PostActivate;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.interceptor.InvocationContext;
import java.util.WeakHashMap;
import org.springframework.beans.factory.access.BeanFactoryReference;
import java.util.Map;

public class SpringBeanAutowiringInterceptor
{
    private final Map<Object, BeanFactoryReference> beanFactoryReferences;
    
    public SpringBeanAutowiringInterceptor() {
        this.beanFactoryReferences = new WeakHashMap<Object, BeanFactoryReference>();
    }
    
    @PostConstruct
    @PostActivate
    public void autowireBean(final InvocationContext invocationContext) {
        this.doAutowireBean(invocationContext.getTarget());
        try {
            invocationContext.proceed();
        }
        catch (RuntimeException ex) {
            this.doReleaseBean(invocationContext.getTarget());
            throw ex;
        }
        catch (Error err) {
            this.doReleaseBean(invocationContext.getTarget());
            throw err;
        }
        catch (Exception ex2) {
            this.doReleaseBean(invocationContext.getTarget());
            throw new EJBException(ex2);
        }
    }
    
    protected void doAutowireBean(final Object target) {
        final AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        this.configureBeanPostProcessor(bpp, target);
        bpp.setBeanFactory(this.getBeanFactory(target));
        bpp.processInjection(target);
    }
    
    protected void configureBeanPostProcessor(final AutowiredAnnotationBeanPostProcessor processor, final Object target) {
    }
    
    protected BeanFactory getBeanFactory(final Object target) {
        BeanFactory factory = this.getBeanFactoryReference(target).getFactory();
        if (factory instanceof ApplicationContext) {
            factory = ((ApplicationContext)factory).getAutowireCapableBeanFactory();
        }
        return factory;
    }
    
    protected BeanFactoryReference getBeanFactoryReference(final Object target) {
        final String key = this.getBeanFactoryLocatorKey(target);
        final BeanFactoryReference ref = this.getBeanFactoryLocator(target).useBeanFactory(key);
        this.beanFactoryReferences.put(target, ref);
        return ref;
    }
    
    protected BeanFactoryLocator getBeanFactoryLocator(final Object target) {
        return ContextSingletonBeanFactoryLocator.getInstance();
    }
    
    protected String getBeanFactoryLocatorKey(final Object target) {
        return null;
    }
    
    @PreDestroy
    @PrePassivate
    public void releaseBean(final InvocationContext invocationContext) {
        this.doReleaseBean(invocationContext.getTarget());
        try {
            invocationContext.proceed();
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (Exception ex2) {
            throw new EJBException(ex2);
        }
    }
    
    protected void doReleaseBean(final Object target) {
        final BeanFactoryReference ref = this.beanFactoryReferences.remove(target);
        if (ref != null) {
            ref.release();
        }
    }
}
