// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.type.AnnotationMetadata;

public interface ImportBeanDefinitionRegistrar
{
    void registerBeanDefinitions(final AnnotationMetadata p0, final BeanDefinitionRegistry p1);
}
