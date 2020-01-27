// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type.classreading;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.asm.Type;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.MultiValueMap;
import java.util.Set;
import org.springframework.core.type.MethodMetadata;
import org.springframework.asm.MethodVisitor;

public class MethodMetadataReadingVisitor extends MethodVisitor implements MethodMetadata
{
    protected final String name;
    protected final int access;
    protected final String declaringClassName;
    protected final ClassLoader classLoader;
    protected final Set<MethodMetadata> methodMetadataSet;
    protected final MultiValueMap<String, AnnotationAttributes> attributeMap;
    
    public MethodMetadataReadingVisitor(final String name, final int access, final String declaringClassName, final ClassLoader classLoader, final Set<MethodMetadata> methodMetadataSet) {
        super(262144);
        this.attributeMap = new LinkedMultiValueMap<String, AnnotationAttributes>(4);
        this.name = name;
        this.access = access;
        this.declaringClassName = declaringClassName;
        this.classLoader = classLoader;
        this.methodMetadataSet = methodMetadataSet;
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        final String className = Type.getType(desc).getClassName();
        this.methodMetadataSet.add(this);
        return new AnnotationAttributesReadingVisitor(className, this.attributeMap, null, this.classLoader);
    }
    
    @Override
    public String getMethodName() {
        return this.name;
    }
    
    @Override
    public boolean isStatic() {
        return (this.access & 0x8) != 0x0;
    }
    
    @Override
    public boolean isFinal() {
        return (this.access & 0x10) != 0x0;
    }
    
    @Override
    public boolean isOverridable() {
        return !this.isStatic() && !this.isFinal() && (this.access & 0x2) == 0x0;
    }
    
    @Override
    public boolean isAnnotated(final String annotationType) {
        return this.attributeMap.containsKey(annotationType);
    }
    
    @Override
    public Map<String, Object> getAnnotationAttributes(final String annotationType) {
        return this.getAnnotationAttributes(annotationType, false);
    }
    
    @Override
    public Map<String, Object> getAnnotationAttributes(final String annotationType, final boolean classValuesAsString) {
        final List<AnnotationAttributes> attributes = this.attributeMap.get(annotationType);
        return (attributes == null) ? null : AnnotationReadingVisitorUtils.convertClassValues(this.classLoader, attributes.get(0), classValuesAsString);
    }
    
    @Override
    public MultiValueMap<String, Object> getAllAnnotationAttributes(final String annotationType) {
        return this.getAllAnnotationAttributes(annotationType, false);
    }
    
    @Override
    public MultiValueMap<String, Object> getAllAnnotationAttributes(final String annotationType, final boolean classValuesAsString) {
        if (!this.attributeMap.containsKey(annotationType)) {
            return null;
        }
        final MultiValueMap<String, Object> allAttributes = new LinkedMultiValueMap<String, Object>();
        for (final AnnotationAttributes annotationAttributes : this.attributeMap.get(annotationType)) {
            for (final Map.Entry<String, Object> entry : AnnotationReadingVisitorUtils.convertClassValues(this.classLoader, annotationAttributes, classValuesAsString).entrySet()) {
                allAttributes.add(entry.getKey(), entry.getValue());
            }
        }
        return allAttributes;
    }
    
    @Override
    public String getDeclaringClassName() {
        return this.declaringClassName;
    }
}
