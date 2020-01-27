// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import java.util.Iterator;
import java.util.Set;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import java.util.HashMap;
import java.util.Map;

public class Jsr330ScopeMetadataResolver implements ScopeMetadataResolver
{
    private final Map<String, String> scopeMap;
    
    public Jsr330ScopeMetadataResolver() {
        this.scopeMap = new HashMap<String, String>();
        this.registerScope("javax.inject.Singleton", "singleton");
    }
    
    public final void registerScope(final Class<?> annotationType, final String scopeName) {
        this.scopeMap.put(annotationType.getName(), scopeName);
    }
    
    public final void registerScope(final String annotationType, final String scopeName) {
        this.scopeMap.put(annotationType, scopeName);
    }
    
    protected String resolveScopeName(final String annotationType) {
        return this.scopeMap.get(annotationType);
    }
    
    @Override
    public ScopeMetadata resolveScopeMetadata(final BeanDefinition definition) {
        final ScopeMetadata metadata = new ScopeMetadata();
        metadata.setScopeName("prototype");
        if (definition instanceof AnnotatedBeanDefinition) {
            final AnnotatedBeanDefinition annDef = (AnnotatedBeanDefinition)definition;
            final Set<String> annTypes = annDef.getMetadata().getAnnotationTypes();
            String found = null;
            for (final String annType : annTypes) {
                final Set<String> metaAnns = annDef.getMetadata().getMetaAnnotationTypes(annType);
                if (metaAnns.contains("javax.inject.Scope")) {
                    if (found != null) {
                        throw new IllegalStateException("Found ambiguous scope annotations on bean class [" + definition.getBeanClassName() + "]: " + found + ", " + annType);
                    }
                    found = annType;
                    final String scopeName = this.resolveScopeName(annType);
                    if (scopeName == null) {
                        throw new IllegalStateException("Unsupported scope annotation - not mapped onto Spring scope name: " + annType);
                    }
                    metadata.setScopeName(scopeName);
                }
            }
        }
        return metadata;
    }
}
