// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type.classreading;

import org.springframework.core.annotation.AnnotationUtils;
import java.lang.reflect.Modifier;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.MultiValueMap;

final class AnnotationAttributesReadingVisitor extends RecursiveAnnotationAttributesVisitor
{
    private final String annotationType;
    private final MultiValueMap<String, AnnotationAttributes> attributesMap;
    private final Map<String, Set<String>> metaAnnotationMap;
    
    public AnnotationAttributesReadingVisitor(final String annotationType, final MultiValueMap<String, AnnotationAttributes> attributesMap, final Map<String, Set<String>> metaAnnotationMap, final ClassLoader classLoader) {
        super(annotationType, new AnnotationAttributes(), classLoader);
        this.annotationType = annotationType;
        this.attributesMap = attributesMap;
        this.metaAnnotationMap = metaAnnotationMap;
    }
    
    public void doVisitEnd(final Class<?> annotationClass) {
        super.doVisitEnd(annotationClass);
        final List<AnnotationAttributes> attributes = this.attributesMap.get(this.annotationType);
        if (attributes == null) {
            this.attributesMap.add(this.annotationType, this.attributes);
        }
        else {
            attributes.add(0, this.attributes);
        }
        final Set<String> metaAnnotationTypeNames = new LinkedHashSet<String>();
        for (final Annotation metaAnnotation : annotationClass.getAnnotations()) {
            this.recursivelyCollectMetaAnnotations(metaAnnotationTypeNames, metaAnnotation);
        }
        if (this.metaAnnotationMap != null) {
            this.metaAnnotationMap.put(annotationClass.getName(), metaAnnotationTypeNames);
        }
    }
    
    private void recursivelyCollectMetaAnnotations(final Set<String> visited, final Annotation annotation) {
        if (visited.add(annotation.annotationType().getName()) && Modifier.isPublic(annotation.annotationType().getModifiers())) {
            this.attributesMap.add(annotation.annotationType().getName(), AnnotationUtils.getAnnotationAttributes(annotation, true, true));
            for (final Annotation metaMetaAnnotation : annotation.annotationType().getAnnotations()) {
                this.recursivelyCollectMetaAnnotations(visited, metaMetaAnnotation);
            }
        }
    }
}
