// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.annotation;

import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import java.util.Map;
import java.lang.reflect.AnnotatedElement;
import org.springframework.util.ClassUtils;
import org.springframework.beans.factory.support.RootBeanDefinition;
import java.util.Iterator;
import org.springframework.util.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.util.ObjectUtils;
import java.lang.reflect.Method;
import org.springframework.core.MethodParameter;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import java.util.Collection;
import org.springframework.util.Assert;
import java.util.LinkedHashSet;
import java.lang.annotation.Annotation;
import java.util.Set;
import org.springframework.beans.factory.support.GenericTypeAwareAutowireCandidateResolver;

public class QualifierAnnotationAutowireCandidateResolver extends GenericTypeAwareAutowireCandidateResolver
{
    private final Set<Class<? extends Annotation>> qualifierTypes;
    private Class<? extends Annotation> valueAnnotationType;
    
    public QualifierAnnotationAutowireCandidateResolver() {
        this.qualifierTypes = new LinkedHashSet<Class<? extends Annotation>>();
        this.valueAnnotationType = Value.class;
        this.qualifierTypes.add(Qualifier.class);
        final ClassLoader cl = QualifierAnnotationAutowireCandidateResolver.class.getClassLoader();
        try {
            this.qualifierTypes.add((Class<? extends Annotation>)cl.loadClass("javax.inject.Qualifier"));
        }
        catch (ClassNotFoundException ex) {}
    }
    
    public QualifierAnnotationAutowireCandidateResolver(final Class<? extends Annotation> qualifierType) {
        this.qualifierTypes = new LinkedHashSet<Class<? extends Annotation>>();
        this.valueAnnotationType = Value.class;
        Assert.notNull(qualifierType, "'qualifierType' must not be null");
        this.qualifierTypes.add(qualifierType);
    }
    
    public QualifierAnnotationAutowireCandidateResolver(final Set<Class<? extends Annotation>> qualifierTypes) {
        this.qualifierTypes = new LinkedHashSet<Class<? extends Annotation>>();
        this.valueAnnotationType = Value.class;
        Assert.notNull(qualifierTypes, "'qualifierTypes' must not be null");
        this.qualifierTypes.addAll(qualifierTypes);
    }
    
    public void addQualifierType(final Class<? extends Annotation> qualifierType) {
        this.qualifierTypes.add(qualifierType);
    }
    
    public void setValueAnnotationType(final Class<? extends Annotation> valueAnnotationType) {
        this.valueAnnotationType = valueAnnotationType;
    }
    
    @Override
    public boolean isAutowireCandidate(final BeanDefinitionHolder bdHolder, final DependencyDescriptor descriptor) {
        boolean match = super.isAutowireCandidate(bdHolder, descriptor);
        if (match && descriptor != null) {
            match = this.checkQualifiers(bdHolder, descriptor.getAnnotations());
            if (match) {
                final MethodParameter methodParam = descriptor.getMethodParameter();
                if (methodParam != null) {
                    final Method method = methodParam.getMethod();
                    if (method == null || Void.TYPE.equals(method.getReturnType())) {
                        match = this.checkQualifiers(bdHolder, methodParam.getMethodAnnotations());
                    }
                }
            }
        }
        return match;
    }
    
    protected boolean checkQualifiers(final BeanDefinitionHolder bdHolder, final Annotation[] annotationsToSearch) {
        if (ObjectUtils.isEmpty(annotationsToSearch)) {
            return true;
        }
        final SimpleTypeConverter typeConverter = new SimpleTypeConverter();
        for (final Annotation annotation : annotationsToSearch) {
            final Class<? extends Annotation> type = annotation.annotationType();
            boolean checkMeta = true;
            boolean fallbackToMeta = false;
            if (this.isQualifier(type)) {
                if (!this.checkQualifier(bdHolder, annotation, typeConverter)) {
                    fallbackToMeta = true;
                }
                else {
                    checkMeta = false;
                }
            }
            if (checkMeta) {
                boolean foundMeta = false;
                for (final Annotation metaAnn : type.getAnnotations()) {
                    final Class<? extends Annotation> metaType = metaAnn.annotationType();
                    if (this.isQualifier(metaType)) {
                        foundMeta = true;
                        if ((fallbackToMeta && StringUtils.isEmpty(AnnotationUtils.getValue(metaAnn))) || !this.checkQualifier(bdHolder, metaAnn, typeConverter)) {
                            return false;
                        }
                    }
                }
                if (fallbackToMeta && !foundMeta) {
                    return false;
                }
            }
        }
        return true;
    }
    
    protected boolean isQualifier(final Class<? extends Annotation> annotationType) {
        for (final Class<? extends Annotation> qualifierType : this.qualifierTypes) {
            if (annotationType.equals(qualifierType) || annotationType.isAnnotationPresent(qualifierType)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean checkQualifier(final BeanDefinitionHolder bdHolder, final Annotation annotation, final TypeConverter typeConverter) {
        final Class<? extends Annotation> type = annotation.annotationType();
        final RootBeanDefinition bd = (RootBeanDefinition)bdHolder.getBeanDefinition();
        AutowireCandidateQualifier qualifier = bd.getQualifier(type.getName());
        if (qualifier == null) {
            qualifier = bd.getQualifier(ClassUtils.getShortName(type));
        }
        if (qualifier == null) {
            Annotation targetAnnotation = this.getFactoryMethodAnnotation(bd, type);
            if (targetAnnotation == null) {
                final RootBeanDefinition dbd = this.getResolvedDecoratedDefinition(bd);
                if (dbd != null) {
                    targetAnnotation = this.getFactoryMethodAnnotation(dbd, type);
                }
            }
            if (targetAnnotation == null) {
                if (this.getBeanFactory() != null) {
                    final Class<?> beanType = this.getBeanFactory().getType(bdHolder.getBeanName());
                    if (beanType != null) {
                        targetAnnotation = AnnotationUtils.getAnnotation(ClassUtils.getUserClass(beanType), type);
                    }
                }
                if (targetAnnotation == null && bd.hasBeanClass()) {
                    targetAnnotation = AnnotationUtils.getAnnotation(ClassUtils.getUserClass(bd.getBeanClass()), type);
                }
            }
            if (targetAnnotation != null && targetAnnotation.equals(annotation)) {
                return true;
            }
        }
        final Map<String, Object> attributes = AnnotationUtils.getAnnotationAttributes(annotation);
        if (attributes.isEmpty() && qualifier == null) {
            return false;
        }
        for (final Map.Entry<String, Object> entry : attributes.entrySet()) {
            final String attributeName = entry.getKey();
            final Object expectedValue = entry.getValue();
            Object actualValue = null;
            if (qualifier != null) {
                actualValue = qualifier.getAttribute(attributeName);
            }
            if (actualValue == null) {
                actualValue = bd.getAttribute(attributeName);
            }
            if (actualValue == null && attributeName.equals(AutowireCandidateQualifier.VALUE_KEY) && expectedValue instanceof String && bdHolder.matchesName((String)expectedValue)) {
                continue;
            }
            if (actualValue == null && qualifier != null) {
                actualValue = AnnotationUtils.getDefaultValue(annotation, attributeName);
            }
            if (actualValue != null) {
                actualValue = typeConverter.convertIfNecessary(actualValue, expectedValue.getClass());
            }
            if (!expectedValue.equals(actualValue)) {
                return false;
            }
        }
        return true;
    }
    
    protected Annotation getFactoryMethodAnnotation(final RootBeanDefinition bd, final Class<? extends Annotation> type) {
        final Method resolvedFactoryMethod = bd.getResolvedFactoryMethod();
        return (resolvedFactoryMethod != null) ? AnnotationUtils.getAnnotation(resolvedFactoryMethod, type) : null;
    }
    
    @Override
    public Object getSuggestedValue(final DependencyDescriptor descriptor) {
        Object value = this.findValue(descriptor.getAnnotations());
        if (value == null) {
            final MethodParameter methodParam = descriptor.getMethodParameter();
            if (methodParam != null) {
                value = this.findValue(methodParam.getMethodAnnotations());
            }
        }
        return value;
    }
    
    protected Object findValue(final Annotation[] annotationsToSearch) {
        for (final Annotation annotation : annotationsToSearch) {
            if (this.valueAnnotationType.isInstance(annotation)) {
                return this.extractValue(annotation);
            }
        }
        for (final Annotation annotation : annotationsToSearch) {
            final Annotation metaAnn = annotation.annotationType().getAnnotation(this.valueAnnotationType);
            if (metaAnn != null) {
                return this.extractValue(metaAnn);
            }
        }
        return null;
    }
    
    protected Object extractValue(final Annotation valueAnnotation) {
        final Object value = AnnotationUtils.getValue(valueAnnotation);
        if (value == null) {
            throw new IllegalStateException("Value annotation must have a value attribute");
        }
        return value;
    }
}
