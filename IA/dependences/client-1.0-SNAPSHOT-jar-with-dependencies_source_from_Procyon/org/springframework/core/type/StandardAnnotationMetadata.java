// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type;

import java.lang.reflect.Method;
import org.springframework.util.MultiValueMap;
import java.util.Map;
import java.lang.reflect.AnnotatedElement;
import org.springframework.core.annotation.AnnotatedElementUtils;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

public class StandardAnnotationMetadata extends StandardClassMetadata implements AnnotationMetadata
{
    private final boolean nestedAnnotationsAsMap;
    
    public StandardAnnotationMetadata(final Class<?> introspectedClass) {
        this(introspectedClass, false);
    }
    
    public StandardAnnotationMetadata(final Class<?> introspectedClass, final boolean nestedAnnotationsAsMap) {
        super(introspectedClass);
        this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
    }
    
    @Override
    public Set<String> getAnnotationTypes() {
        final Set<String> types = new LinkedHashSet<String>();
        final Annotation[] annotations;
        final Annotation[] anns = annotations = this.getIntrospectedClass().getAnnotations();
        for (final Annotation ann : annotations) {
            types.add(ann.annotationType().getName());
        }
        return types;
    }
    
    @Override
    public Set<String> getMetaAnnotationTypes(final String annotationType) {
        return AnnotatedElementUtils.getMetaAnnotationTypes(this.getIntrospectedClass(), annotationType);
    }
    
    @Override
    public boolean hasAnnotation(final String annotationType) {
        final Annotation[] annotations;
        final Annotation[] anns = annotations = this.getIntrospectedClass().getAnnotations();
        for (final Annotation ann : annotations) {
            if (ann.annotationType().getName().equals(annotationType)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean hasMetaAnnotation(final String annotationType) {
        return AnnotatedElementUtils.hasMetaAnnotationTypes(this.getIntrospectedClass(), annotationType);
    }
    
    @Override
    public boolean isAnnotated(final String annotationType) {
        return AnnotatedElementUtils.isAnnotated(this.getIntrospectedClass(), annotationType);
    }
    
    @Override
    public Map<String, Object> getAnnotationAttributes(final String annotationType) {
        return this.getAnnotationAttributes(annotationType, false);
    }
    
    @Override
    public Map<String, Object> getAnnotationAttributes(final String annotationType, final boolean classValuesAsString) {
        return AnnotatedElementUtils.getAnnotationAttributes(this.getIntrospectedClass(), annotationType, classValuesAsString, this.nestedAnnotationsAsMap);
    }
    
    @Override
    public MultiValueMap<String, Object> getAllAnnotationAttributes(final String annotationType) {
        return this.getAllAnnotationAttributes(annotationType, false);
    }
    
    @Override
    public MultiValueMap<String, Object> getAllAnnotationAttributes(final String annotationType, final boolean classValuesAsString) {
        return AnnotatedElementUtils.getAllAnnotationAttributes(this.getIntrospectedClass(), annotationType, classValuesAsString, this.nestedAnnotationsAsMap);
    }
    
    @Override
    public boolean hasAnnotatedMethods(final String annotationType) {
        final Method[] declaredMethods;
        final Method[] methods = declaredMethods = this.getIntrospectedClass().getDeclaredMethods();
        for (final Method method : declaredMethods) {
            if (AnnotatedElementUtils.isAnnotated(method, annotationType)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Set<MethodMetadata> getAnnotatedMethods(final String annotationType) {
        final Method[] methods = this.getIntrospectedClass().getDeclaredMethods();
        final Set<MethodMetadata> annotatedMethods = new LinkedHashSet<MethodMetadata>();
        for (final Method method : methods) {
            if (AnnotatedElementUtils.isAnnotated(method, annotationType)) {
                annotatedMethods.add(new StandardMethodMetadata(method, this.nestedAnnotationsAsMap));
            }
        }
        return annotatedMethods;
    }
}
