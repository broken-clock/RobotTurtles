// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type.classreading;

import org.springframework.asm.Attribute;
import org.springframework.asm.FieldVisitor;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import org.springframework.asm.Type;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.util.LinkedMultiValueMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.MultiValueMap;
import java.util.Map;
import java.util.Set;
import org.springframework.core.type.AnnotationMetadata;

public class AnnotationMetadataReadingVisitor extends ClassMetadataReadingVisitor implements AnnotationMetadata
{
    protected final ClassLoader classLoader;
    protected final Set<String> annotationSet;
    protected final Map<String, Set<String>> metaAnnotationMap;
    protected final MultiValueMap<String, AnnotationAttributes> attributeMap;
    protected final Set<MethodMetadata> methodMetadataSet;
    
    public AnnotationMetadataReadingVisitor(final ClassLoader classLoader) {
        this.annotationSet = new LinkedHashSet<String>(4);
        this.metaAnnotationMap = new LinkedHashMap<String, Set<String>>(4);
        this.attributeMap = new LinkedMultiValueMap<String, AnnotationAttributes>(4);
        this.methodMetadataSet = new LinkedHashSet<MethodMetadata>(4);
        this.classLoader = classLoader;
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        return new MethodMetadataReadingVisitor(name, access, this.getClassName(), this.classLoader, this.methodMetadataSet);
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        final String className = Type.getType(desc).getClassName();
        this.annotationSet.add(className);
        return new AnnotationAttributesReadingVisitor(className, this.attributeMap, this.metaAnnotationMap, this.classLoader);
    }
    
    @Override
    public Set<String> getAnnotationTypes() {
        return this.annotationSet;
    }
    
    @Override
    public Set<String> getMetaAnnotationTypes(final String annotationType) {
        return this.metaAnnotationMap.get(annotationType);
    }
    
    @Override
    public boolean hasAnnotation(final String annotationType) {
        return this.annotationSet.contains(annotationType);
    }
    
    @Override
    public boolean hasMetaAnnotation(final String metaAnnotationType) {
        final Collection<Set<String>> allMetaTypes = this.metaAnnotationMap.values();
        for (final Set<String> metaTypes : allMetaTypes) {
            if (metaTypes.contains(metaAnnotationType)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isAnnotated(final String annotationType) {
        return this.attributeMap.containsKey(annotationType);
    }
    
    @Override
    public AnnotationAttributes getAnnotationAttributes(final String annotationType) {
        return this.getAnnotationAttributes(annotationType, false);
    }
    
    @Override
    public AnnotationAttributes getAnnotationAttributes(final String annotationType, final boolean classValuesAsString) {
        final List<AnnotationAttributes> attributes = this.attributeMap.get(annotationType);
        final AnnotationAttributes raw = (attributes == null) ? null : attributes.get(0);
        return AnnotationReadingVisitorUtils.convertClassValues(this.classLoader, raw, classValuesAsString);
    }
    
    @Override
    public MultiValueMap<String, Object> getAllAnnotationAttributes(final String annotationType) {
        return this.getAllAnnotationAttributes(annotationType, false);
    }
    
    @Override
    public MultiValueMap<String, Object> getAllAnnotationAttributes(final String annotationType, final boolean classValuesAsString) {
        final MultiValueMap<String, Object> allAttributes = new LinkedMultiValueMap<String, Object>();
        final List<AnnotationAttributes> attributes = this.attributeMap.get(annotationType);
        if (attributes == null) {
            return null;
        }
        for (final AnnotationAttributes raw : attributes) {
            for (final Map.Entry<String, Object> entry : AnnotationReadingVisitorUtils.convertClassValues(this.classLoader, raw, classValuesAsString).entrySet()) {
                allAttributes.add(entry.getKey(), entry.getValue());
            }
        }
        return allAttributes;
    }
    
    @Override
    public boolean hasAnnotatedMethods(final String annotationType) {
        for (final MethodMetadata methodMetadata : this.methodMetadataSet) {
            if (methodMetadata.isAnnotated(annotationType)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Set<MethodMetadata> getAnnotatedMethods(final String annotationType) {
        final Set<MethodMetadata> annotatedMethods = new LinkedHashSet<MethodMetadata>(4);
        for (final MethodMetadata methodMetadata : this.methodMetadataSet) {
            if (methodMetadata.isAnnotated(annotationType)) {
                annotatedMethods.add(methodMetadata);
            }
        }
        return annotatedMethods;
    }
}
