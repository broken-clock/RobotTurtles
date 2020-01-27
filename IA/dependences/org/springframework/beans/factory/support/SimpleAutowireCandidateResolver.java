// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.BeanDefinitionHolder;

public class SimpleAutowireCandidateResolver implements AutowireCandidateResolver
{
    @Override
    public boolean isAutowireCandidate(final BeanDefinitionHolder bdHolder, final DependencyDescriptor descriptor) {
        return bdHolder.getBeanDefinition().isAutowireCandidate();
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
