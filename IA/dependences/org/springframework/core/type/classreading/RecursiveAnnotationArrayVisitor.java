// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.type.classreading;

import java.util.HashMap;
import java.util.LinkedHashMap;
import org.springframework.asm.Type;
import org.springframework.asm.AnnotationVisitor;
import java.lang.reflect.Array;
import org.springframework.util.ObjectUtils;
import java.util.ArrayList;
import org.springframework.core.annotation.AnnotationAttributes;
import java.util.List;

final class RecursiveAnnotationArrayVisitor extends AbstractRecursiveAnnotationVisitor
{
    private final String attributeName;
    private final List<AnnotationAttributes> allNestedAttributes;
    
    public RecursiveAnnotationArrayVisitor(final String attributeName, final AnnotationAttributes attributes, final ClassLoader classLoader) {
        super(classLoader, attributes);
        this.allNestedAttributes = new ArrayList<AnnotationAttributes>();
        this.attributeName = attributeName;
    }
    
    @Override
    public void visit(final String attributeName, final Object attributeValue) {
        Object newValue = attributeValue;
        final Object existingValue = ((LinkedHashMap<K, Object>)this.attributes).get(this.attributeName);
        if (existingValue != null) {
            newValue = ObjectUtils.addObjectToArray((Object[])existingValue, newValue);
        }
        else {
            Class<?> arrayClass = newValue.getClass();
            if (Enum.class.isAssignableFrom(arrayClass)) {
                while (arrayClass.getSuperclass() != null && !arrayClass.isEnum()) {
                    arrayClass = arrayClass.getSuperclass();
                }
            }
            final Object[] newArray = (Object[])Array.newInstance(arrayClass, 1);
            newArray[0] = newValue;
            newValue = newArray;
        }
        this.attributes.put(this.attributeName, newValue);
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String attributeName, final String asmTypeDescriptor) {
        final String annotationType = Type.getType(asmTypeDescriptor).getClassName();
        final AnnotationAttributes nestedAttributes = new AnnotationAttributes();
        this.allNestedAttributes.add(nestedAttributes);
        return new RecursiveAnnotationAttributesVisitor(annotationType, nestedAttributes, this.classLoader);
    }
    
    @Override
    public void visitEnd() {
        if (!this.allNestedAttributes.isEmpty()) {
            ((HashMap<String, AnnotationAttributes[]>)this.attributes).put(this.attributeName, this.allNestedAttributes.toArray(new AnnotationAttributes[this.allNestedAttributes.size()]));
        }
    }
}
