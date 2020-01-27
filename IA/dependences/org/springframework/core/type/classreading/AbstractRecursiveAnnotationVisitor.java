// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type.classreading;

import java.util.HashMap;
import java.lang.reflect.Field;
import org.springframework.util.ReflectionUtils;
import org.springframework.asm.Type;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.AnnotationAttributes;
import org.apache.commons.logging.Log;
import org.springframework.asm.AnnotationVisitor;

abstract class AbstractRecursiveAnnotationVisitor extends AnnotationVisitor
{
    protected final Log logger;
    protected final AnnotationAttributes attributes;
    protected final ClassLoader classLoader;
    
    public AbstractRecursiveAnnotationVisitor(final ClassLoader classLoader, final AnnotationAttributes attributes) {
        super(262144);
        this.logger = LogFactory.getLog(this.getClass());
        this.classLoader = classLoader;
        this.attributes = attributes;
    }
    
    @Override
    public void visit(final String attributeName, final Object attributeValue) {
        this.attributes.put(attributeName, attributeValue);
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String attributeName, final String asmTypeDescriptor) {
        final String annotationType = Type.getType(asmTypeDescriptor).getClassName();
        final AnnotationAttributes nestedAttributes = new AnnotationAttributes();
        ((HashMap<String, AnnotationAttributes>)this.attributes).put(attributeName, nestedAttributes);
        return new RecursiveAnnotationAttributesVisitor(annotationType, nestedAttributes, this.classLoader);
    }
    
    @Override
    public AnnotationVisitor visitArray(final String attributeName) {
        return new RecursiveAnnotationArrayVisitor(attributeName, this.attributes, this.classLoader);
    }
    
    @Override
    public void visitEnum(final String attributeName, final String asmTypeDescriptor, final String attributeValue) {
        final Object newValue = this.getEnumValue(asmTypeDescriptor, attributeValue);
        this.visit(attributeName, newValue);
    }
    
    protected Object getEnumValue(final String asmTypeDescriptor, final String attributeValue) {
        Object valueToUse = attributeValue;
        try {
            final Class<?> enumType = this.classLoader.loadClass(Type.getType(asmTypeDescriptor).getClassName());
            final Field enumConstant = ReflectionUtils.findField(enumType, attributeValue);
            if (enumConstant != null) {
                valueToUse = enumConstant.get(null);
            }
        }
        catch (ClassNotFoundException ex) {
            this.logger.debug("Failed to classload enum type while reading annotation metadata", ex);
        }
        catch (IllegalAccessException ex2) {
            this.logger.warn("Could not access enum value while reading annotation metadata", ex2);
        }
        return valueToUse;
    }
}
