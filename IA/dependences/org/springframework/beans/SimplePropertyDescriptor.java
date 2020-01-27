// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import java.beans.IntrospectionException;
import java.lang.reflect.Method;
import java.beans.PropertyDescriptor;

class SimplePropertyDescriptor extends PropertyDescriptor
{
    private Method readMethod;
    private Method writeMethod;
    private Class<?> propertyType;
    private Class<?> propertyEditorClass;
    
    public SimplePropertyDescriptor(final PropertyDescriptor original) throws IntrospectionException {
        this(original.getName(), original.getReadMethod(), original.getWriteMethod());
        PropertyDescriptorUtils.copyNonMethodProperties(original, this);
    }
    
    public SimplePropertyDescriptor(final String propertyName, final Method readMethod, final Method writeMethod) throws IntrospectionException {
        super(propertyName, null, null);
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
        this.propertyType = PropertyDescriptorUtils.findPropertyType(readMethod, writeMethod);
    }
    
    @Override
    public Method getReadMethod() {
        return this.readMethod;
    }
    
    @Override
    public void setReadMethod(final Method readMethod) {
        this.readMethod = readMethod;
    }
    
    @Override
    public Method getWriteMethod() {
        return this.writeMethod;
    }
    
    @Override
    public void setWriteMethod(final Method writeMethod) {
        this.writeMethod = writeMethod;
    }
    
    @Override
    public Class<?> getPropertyType() {
        if (this.propertyType == null) {
            try {
                this.propertyType = PropertyDescriptorUtils.findPropertyType(this.readMethod, this.writeMethod);
            }
            catch (IntrospectionException ex) {}
        }
        return this.propertyType;
    }
    
    @Override
    public Class<?> getPropertyEditorClass() {
        return this.propertyEditorClass;
    }
    
    @Override
    public void setPropertyEditorClass(final Class<?> propertyEditorClass) {
        this.propertyEditorClass = propertyEditorClass;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return PropertyDescriptorUtils.equals(this, obj);
    }
    
    @Override
    public String toString() {
        return String.format("%s[name=%s, propertyType=%s, readMethod=%s, writeMethod=%s]", this.getClass().getSimpleName(), this.getName(), this.getPropertyType(), this.readMethod, this.writeMethod);
    }
}
