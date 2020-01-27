// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.annotation;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.beans.factory.config.BeanDefinition;

public interface AnnotatedBeanDefinition extends BeanDefinition
{
    AnnotationMetadata getMetadata();
}
