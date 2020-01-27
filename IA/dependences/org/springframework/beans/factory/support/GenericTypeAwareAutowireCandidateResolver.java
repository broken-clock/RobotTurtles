// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import java.lang.reflect.Method;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.ResolvableType;
import org.springframework.util.ClassUtils;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public class GenericTypeAwareAutowireCandidateResolver implements AutowireCandidateResolver, BeanFactoryAware
{
    private BeanFactory beanFactory;
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    
    protected final BeanFactory getBeanFactory() {
        return this.beanFactory;
    }
    
    @Override
    public boolean isAutowireCandidate(final BeanDefinitionHolder bdHolder, final DependencyDescriptor descriptor) {
        return bdHolder.getBeanDefinition().isAutowireCandidate() && (descriptor == null || this.checkGenericTypeMatch(bdHolder, descriptor));
    }
    
    protected boolean checkGenericTypeMatch(final BeanDefinitionHolder bdHolder, final DependencyDescriptor descriptor) {
        final ResolvableType dependencyType = descriptor.getResolvableType();
        if (dependencyType.getType() instanceof Class) {
            return true;
        }
        ResolvableType targetType = null;
        RootBeanDefinition rbd = null;
        if (bdHolder.getBeanDefinition() instanceof RootBeanDefinition) {
            rbd = (RootBeanDefinition)bdHolder.getBeanDefinition();
        }
        if (rbd != null) {
            targetType = this.getReturnTypeForFactoryMethod(rbd, descriptor);
            if (targetType == null) {
                final RootBeanDefinition dbd = this.getResolvedDecoratedDefinition(rbd);
                if (dbd != null) {
                    targetType = this.getReturnTypeForFactoryMethod(dbd, descriptor);
                }
            }
        }
        if (targetType == null) {
            if (this.beanFactory != null) {
                final Class<?> beanType = this.beanFactory.getType(bdHolder.getBeanName());
                if (beanType != null) {
                    targetType = ResolvableType.forClass(ClassUtils.getUserClass(beanType));
                }
            }
            if (targetType == null && rbd != null && rbd.hasBeanClass() && rbd.getFactoryMethodName() == null) {
                final Class<?> beanClass = rbd.getBeanClass();
                if (!FactoryBean.class.isAssignableFrom(beanClass)) {
                    targetType = ResolvableType.forClass(ClassUtils.getUserClass(beanClass));
                }
            }
        }
        return targetType == null || (descriptor.fallbackMatchAllowed() && targetType.hasUnresolvableGenerics()) || dependencyType.isAssignableFrom(targetType);
    }
    
    protected RootBeanDefinition getResolvedDecoratedDefinition(final RootBeanDefinition rbd) {
        final BeanDefinitionHolder decDef = rbd.getDecoratedDefinition();
        if (decDef != null && this.beanFactory instanceof ConfigurableListableBeanFactory) {
            final ConfigurableListableBeanFactory clbf = (ConfigurableListableBeanFactory)this.beanFactory;
            if (clbf.containsBeanDefinition(decDef.getBeanName())) {
                final BeanDefinition dbd = clbf.getMergedBeanDefinition(decDef.getBeanName());
                if (dbd instanceof RootBeanDefinition) {
                    return (RootBeanDefinition)dbd;
                }
            }
        }
        return null;
    }
    
    protected ResolvableType getReturnTypeForFactoryMethod(final RootBeanDefinition rbd, final DependencyDescriptor descriptor) {
        final Class<?> preResolved = rbd.resolvedFactoryMethodReturnType;
        if (preResolved != null) {
            return ResolvableType.forClass(preResolved);
        }
        final Method resolvedFactoryMethod = rbd.getResolvedFactoryMethod();
        if (resolvedFactoryMethod != null && descriptor.getDependencyType().isAssignableFrom(resolvedFactoryMethod.getReturnType())) {
            return ResolvableType.forMethodReturnType(resolvedFactoryMethod);
        }
        return null;
    }
    
    @Override
    public Object getSuggestedValue(final DependencyDescriptor descriptor) {
        return null;
    }
    
    @Override
    public Object getLazyResolutionProxyIfNecessary(final DependencyDescriptor descriptor, final String beanName) {
        return null;
    }
}
