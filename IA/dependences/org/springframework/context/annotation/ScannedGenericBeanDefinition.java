// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.util.Assert;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;

public class ScannedGenericBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition
{
    private final AnnotationMetadata metadata;
    
    public ScannedGenericBeanDefinition(final MetadataReader metadataReader) {
        Assert.notNull(metadataReader, "MetadataReader must not be null");
        this.metadata = metadataReader.getAnnotationMetadata();
        this.setBeanClassName(this.metadata.getClassName());
    }
    
    @Override
    public final AnnotationMetadata getMetadata() {
        return this.metadata;
    }
}
