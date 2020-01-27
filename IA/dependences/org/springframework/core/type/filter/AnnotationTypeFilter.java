// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type.filter;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import java.lang.annotation.Inherited;
import java.lang.annotation.Annotation;

public class AnnotationTypeFilter extends AbstractTypeHierarchyTraversingFilter
{
    private final Class<? extends Annotation> annotationType;
    private final boolean considerMetaAnnotations;
    
    public AnnotationTypeFilter(final Class<? extends Annotation> annotationType) {
        this(annotationType, true);
    }
    
    public AnnotationTypeFilter(final Class<? extends Annotation> annotationType, final boolean considerMetaAnnotations) {
        this(annotationType, considerMetaAnnotations, false);
    }
    
    public AnnotationTypeFilter(final Class<? extends Annotation> annotationType, final boolean considerMetaAnnotations, final boolean considerInterfaces) {
        super(annotationType.isAnnotationPresent(Inherited.class), considerInterfaces);
        this.annotationType = annotationType;
        this.considerMetaAnnotations = considerMetaAnnotations;
    }
    
    @Override
    protected boolean matchSelf(final MetadataReader metadataReader) {
        final AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
        return metadata.hasAnnotation(this.annotationType.getName()) || (this.considerMetaAnnotations && metadata.hasMetaAnnotation(this.annotationType.getName()));
    }
    
    @Override
    protected Boolean matchSuperClass(final String superClassName) {
        if (Object.class.getName().equals(superClassName)) {
            return Boolean.FALSE;
        }
        if (superClassName.startsWith("java.")) {
            try {
                final Class<?> clazz = this.getClass().getClassLoader().loadClass(superClassName);
                return clazz.getAnnotation(this.annotationType) != null;
            }
            catch (ClassNotFoundException ex) {}
        }
        return null;
    }
}
