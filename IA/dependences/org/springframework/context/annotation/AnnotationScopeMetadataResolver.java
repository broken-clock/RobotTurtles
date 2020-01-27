// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.util.Assert;
import java.lang.annotation.Annotation;

public class AnnotationScopeMetadataResolver implements ScopeMetadataResolver
{
    private final ScopedProxyMode defaultProxyMode;
    protected Class<? extends Annotation> scopeAnnotationType;
    
    public AnnotationScopeMetadataResolver() {
        this.scopeAnnotationType = Scope.class;
        this.defaultProxyMode = ScopedProxyMode.NO;
    }
    
    public AnnotationScopeMetadataResolver(final ScopedProxyMode defaultProxyMode) {
        this.scopeAnnotationType = Scope.class;
        Assert.notNull(defaultProxyMode, "'defaultProxyMode' must not be null");
        this.defaultProxyMode = defaultProxyMode;
    }
    
    public void setScopeAnnotationType(final Class<? extends Annotation> scopeAnnotationType) {
        Assert.notNull(scopeAnnotationType, "'scopeAnnotationType' must not be null");
        this.scopeAnnotationType = scopeAnnotationType;
    }
    
    @Override
    public ScopeMetadata resolveScopeMetadata(final BeanDefinition definition) {
        final ScopeMetadata metadata = new ScopeMetadata();
        if (definition instanceof AnnotatedBeanDefinition) {
            final AnnotatedBeanDefinition annDef = (AnnotatedBeanDefinition)definition;
            final AnnotationAttributes attributes = AnnotationConfigUtils.attributesFor(annDef.getMetadata(), this.scopeAnnotationType);
            if (attributes != null) {
                metadata.setScopeName(attributes.getString("value"));
                ScopedProxyMode proxyMode = attributes.getEnum("proxyMode");
                if (proxyMode == null || proxyMode == ScopedProxyMode.DEFAULT) {
                    proxyMode = this.defaultProxyMode;
                }
                metadata.setScopedProxyMode(proxyMode);
            }
        }
        return metadata;
    }
}
