// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.TypeConverter;
import java.util.Set;
import org.springframework.aop.TargetSource;
import org.springframework.util.Assert;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import java.lang.reflect.Method;
import org.springframework.core.MethodParameter;
import java.lang.annotation.Annotation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;

public class ContextAnnotationAutowireCandidateResolver extends QualifierAnnotationAutowireCandidateResolver
{
    @Override
    public Object getLazyResolutionProxyIfNecessary(final DependencyDescriptor descriptor, final String beanName) {
        return this.isLazy(descriptor) ? this.buildLazyResolutionProxy(descriptor, beanName) : null;
    }
    
    protected boolean isLazy(final DependencyDescriptor descriptor) {
        for (final Annotation ann : descriptor.getAnnotations()) {
            final Lazy lazy = AnnotationUtils.getAnnotation(ann, Lazy.class);
            if (lazy != null && lazy.value()) {
                return true;
            }
        }
        final MethodParameter methodParam = descriptor.getMethodParameter();
        if (methodParam != null) {
            final Method method = methodParam.getMethod();
            if (method == null || Void.TYPE.equals(method.getReturnType())) {
                final Lazy lazy2 = AnnotationUtils.getAnnotation(methodParam.getAnnotatedElement(), Lazy.class);
                if (lazy2 != null && lazy2.value()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected Object buildLazyResolutionProxy(final DependencyDescriptor descriptor, final String beanName) {
        Assert.state(this.getBeanFactory() instanceof DefaultListableBeanFactory, "BeanFactory needs to be a DefaultListableBeanFactory");
        final DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)this.getBeanFactory();
        final TargetSource ts = new TargetSource() {
            @Override
            public Class<?> getTargetClass() {
                return descriptor.getDependencyType();
            }
            
            @Override
            public boolean isStatic() {
                return false;
            }
            
            @Override
            public Object getTarget() {
                return beanFactory.doResolveDependency(descriptor, beanName, null, null);
            }
            
            @Override
            public void releaseTarget(final Object target) {
            }
        };
        final ProxyFactory pf = new ProxyFactory();
        pf.setTargetSource(ts);
        final Class<?> dependencyType = descriptor.getDependencyType();
        if (dependencyType.isInterface()) {
            pf.addInterface(dependencyType);
        }
        return pf.getProxy(beanFactory.getBeanClassLoader());
    }
}
