// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.annotation;

import java.util.Iterator;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.BeanFactory;
import java.util.HashSet;
import org.springframework.util.Assert;
import java.util.Set;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.concurrent.Executor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.aop.Pointcut;
import org.aopalliance.aop.Advice;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.aop.support.AbstractPointcutAdvisor;

public class AsyncAnnotationAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware
{
    private Advice advice;
    private Pointcut pointcut;
    
    public AsyncAnnotationAdvisor() {
        this(new SimpleAsyncTaskExecutor());
    }
    
    public AsyncAnnotationAdvisor(final Executor executor) {
        final Set<Class<? extends Annotation>> asyncAnnotationTypes = new LinkedHashSet<Class<? extends Annotation>>(2);
        asyncAnnotationTypes.add(Async.class);
        final ClassLoader cl = AsyncAnnotationAdvisor.class.getClassLoader();
        try {
            asyncAnnotationTypes.add((Class<? extends Annotation>)cl.loadClass("javax.ejb.Asynchronous"));
        }
        catch (ClassNotFoundException ex) {}
        this.advice = this.buildAdvice(executor);
        this.pointcut = this.buildPointcut(asyncAnnotationTypes);
    }
    
    public void setTaskExecutor(final Executor executor) {
        this.advice = this.buildAdvice(executor);
    }
    
    public void setAsyncAnnotationType(final Class<? extends Annotation> asyncAnnotationType) {
        Assert.notNull(asyncAnnotationType, "'asyncAnnotationType' must not be null");
        final Set<Class<? extends Annotation>> asyncAnnotationTypes = new HashSet<Class<? extends Annotation>>();
        asyncAnnotationTypes.add(asyncAnnotationType);
        this.pointcut = this.buildPointcut(asyncAnnotationTypes);
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware)this.advice).setBeanFactory(beanFactory);
        }
    }
    
    @Override
    public Advice getAdvice() {
        return this.advice;
    }
    
    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }
    
    protected Advice buildAdvice(final Executor executor) {
        return new AnnotationAsyncExecutionInterceptor(executor);
    }
    
    protected Pointcut buildPointcut(final Set<Class<? extends Annotation>> asyncAnnotationTypes) {
        ComposablePointcut result = null;
        for (final Class<? extends Annotation> asyncAnnotationType : asyncAnnotationTypes) {
            final Pointcut cpc = new AnnotationMatchingPointcut(asyncAnnotationType, true);
            final Pointcut mpc = AnnotationMatchingPointcut.forMethodAnnotation(asyncAnnotationType);
            if (result == null) {
                result = new ComposablePointcut(cpc).union(mpc);
            }
            else {
                result.union(cpc).union(mpc);
            }
        }
        return result;
    }
}
