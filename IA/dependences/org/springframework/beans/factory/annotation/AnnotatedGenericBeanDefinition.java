// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.annotation;

import org.springframework.util.Assert;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.beans.factory.support.GenericBeanDefinition;

public class AnnotatedGenericBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition
{
    private final AnnotationMetadata metadata;
    
    public AnnotatedGenericBeanDefinition(final Class<?> beanClass) {
        this.setBeanClass(beanClass);
        this.metadata = new StandardAnnotationMetadata(beanClass, true);
    }
    
    public AnnotatedGenericBeanDefinition(final AnnotationMetadata metadata) {
        Assert.notNull(metadata, "AnnotationMetadata must not be null");
        if (metadata instanceof StandardAnnotationMetadata) {
            this.setBeanClass(((StandardAnnotationMetadata)metadata).getIntrospectedClass());
        }
        else {
            this.setBeanClassName(metadata.getClassName());
        }
        this.metadata = metadata;
    }
    
    @Override
    public final AnnotationMetadata getMetadata() {
        return this.metadata;
    }
}
