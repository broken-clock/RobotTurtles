// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.BeanDefinitionHolder;

public interface AutowireCandidateResolver
{
    boolean isAutowireCandidate(final BeanDefinitionHolder p0, final DependencyDescriptor p1);
    
    Object getSuggestedValue(final DependencyDescriptor p0);
    
    Object getLazyResolutionProxyIfNecessary(final DependencyDescriptor p0, final String p1);
}
