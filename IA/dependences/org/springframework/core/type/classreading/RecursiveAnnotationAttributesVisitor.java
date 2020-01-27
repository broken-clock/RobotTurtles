// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type.classreading;

import java.lang.reflect.Method;
import java.util.Map;
import org.springframework.core.annotation.AnnotationUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import org.springframework.core.annotation.AnnotationAttributes;

class RecursiveAnnotationAttributesVisitor extends AbstractRecursiveAnnotationVisitor
{
    private final String annotationType;
    
    public RecursiveAnnotationAttributesVisitor(final String annotationType, final AnnotationAttributes attributes, final ClassLoader classLoader) {
        super(classLoader, attributes);
        this.annotationType = annotationType;
    }
    
    @Override
    public final void visitEnd() {
        try {
            final Class<?> annotationClass = this.classLoader.loadClass(this.annotationType);
            this.doVisitEnd(annotationClass);
        }
        catch (ClassNotFoundException ex) {
            this.logger.debug("Failed to class-load type while reading annotation metadata. This is a non-fatal error, but certain annotation metadata may be unavailable.", ex);
        }
    }
    
    protected void doVisitEnd(final Class<?> annotationClass) {
        this.registerDefaultValues(annotationClass);
    }
    
    private void registerDefaultValues(final Class<?> annotationClass) {
        if (Modifier.isPublic(annotationClass.getModifiers())) {
            final Method[] methods;
            final Method[] annotationAttributes = methods = annotationClass.getMethods();
            for (final Method annotationAttribute : methods) {
                final String attributeName = annotationAttribute.getName();
                Object defaultValue = annotationAttribute.getDefaultValue();
                if (defaultValue != null && !this.attributes.containsKey(attributeName)) {
                    if (defaultValue instanceof Annotation) {
                        defaultValue = AnnotationAttributes.fromMap(AnnotationUtils.getAnnotationAttributes((Annotation)defaultValue, false, true));
                    }
                    else if (defaultValue instanceof Annotation[]) {
                        final Annotation[] realAnnotations = (Annotation[])defaultValue;
                        final AnnotationAttributes[] mappedAnnotations = new AnnotationAttributes[realAnnotations.length];
                        for (int i = 0; i < realAnnotations.length; ++i) {
                            mappedAnnotations[i] = AnnotationAttributes.fromMap(AnnotationUtils.getAnnotationAttributes(realAnnotations[i], false, true));
                        }
                        defaultValue = mappedAnnotations;
                    }
                    this.attributes.put(attributeName, defaultValue);
                }
            }
        }
    }
}
