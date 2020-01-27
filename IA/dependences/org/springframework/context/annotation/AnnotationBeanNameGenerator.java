// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import java.util.LinkedHashMap;
import java.beans.Introspector;
import org.springframework.util.ClassUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import java.util.Iterator;
import java.util.Set;
import org.springframework.core.type.AnnotationMetadata;
import java.util.Map;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanNameGenerator;

public class AnnotationBeanNameGenerator implements BeanNameGenerator
{
    private static final String COMPONENT_ANNOTATION_CLASSNAME = "org.springframework.stereotype.Component";
    
    @Override
    public String generateBeanName(final BeanDefinition definition, final BeanDefinitionRegistry registry) {
        if (definition instanceof AnnotatedBeanDefinition) {
            final String beanName = this.determineBeanNameFromAnnotation((AnnotatedBeanDefinition)definition);
            if (StringUtils.hasText(beanName)) {
                return beanName;
            }
        }
        return this.buildDefaultBeanName(definition, registry);
    }
    
    protected String determineBeanNameFromAnnotation(final AnnotatedBeanDefinition annotatedDef) {
        final AnnotationMetadata amd = annotatedDef.getMetadata();
        final Set<String> types = amd.getAnnotationTypes();
        String beanName = null;
        for (final String type : types) {
            final AnnotationAttributes attributes = AnnotationConfigUtils.attributesFor(amd, type);
            if (this.isStereotypeWithNameValue(type, amd.getMetaAnnotationTypes(type), attributes)) {
                final Object value = ((LinkedHashMap<K, Object>)attributes).get("value");
                if (!(value instanceof String)) {
                    continue;
                }
                final String strVal = (String)value;
                if (!StringUtils.hasLength(strVal)) {
                    continue;
                }
                if (beanName != null && !strVal.equals(beanName)) {
                    throw new IllegalStateException("Stereotype annotations suggest inconsistent component names: '" + beanName + "' versus '" + strVal + "'");
                }
                beanName = strVal;
            }
        }
        return beanName;
    }
    
    protected boolean isStereotypeWithNameValue(final String annotationType, final Set<String> metaAnnotationTypes, final Map<String, Object> attributes) {
        final boolean isStereotype = annotationType.equals("org.springframework.stereotype.Component") || (metaAnnotationTypes != null && metaAnnotationTypes.contains("org.springframework.stereotype.Component")) || annotationType.equals("javax.annotation.ManagedBean") || annotationType.equals("javax.inject.Named");
        return isStereotype && attributes != null && attributes.containsKey("value");
    }
    
    protected String buildDefaultBeanName(final BeanDefinition definition, final BeanDefinitionRegistry registry) {
        return this.buildDefaultBeanName(definition);
    }
    
    protected String buildDefaultBeanName(final BeanDefinition definition) {
        final String shortClassName = ClassUtils.getShortName(definition.getBeanClassName());
        return Introspector.decapitalize(shortClassName);
    }
}
