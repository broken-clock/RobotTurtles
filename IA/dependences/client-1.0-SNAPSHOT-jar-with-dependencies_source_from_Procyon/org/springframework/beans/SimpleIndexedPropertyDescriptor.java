// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.beans.IndexedPropertyDescriptor;

class SimpleIndexedPropertyDescriptor extends IndexedPropertyDescriptor
{
    private Method readMethod;
    private Method writeMethod;
    private Class<?> propertyType;
    private Method indexedReadMethod;
    private Method indexedWriteMethod;
    private Class<?> indexedPropertyType;
    private Class<?> propertyEditorClass;
    
    public SimpleIndexedPropertyDescriptor(final IndexedPropertyDescriptor original) throws IntrospectionException {
        this(original.getName(), original.getReadMethod(), original.getWriteMethod(), original.getIndexedReadMethod(), original.getIndexedWriteMethod());
        PropertyDescriptorUtils.copyNonMethodProperties(original, this);
    }
    
    public SimpleIndexedPropertyDescriptor(final String propertyName, final Method readMethod, final Method writeMethod, final Method indexedReadMethod, final Method indexedWriteMethod) throws IntrospectionException {
        super(propertyName, null, null, null, null);
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
        this.propertyType = PropertyDescriptorUtils.findPropertyType(readMethod, writeMethod);
        this.indexedReadMethod = indexedReadMethod;
        this.indexedWriteMethod = indexedWriteMethod;
        this.indexedPropertyType = PropertyDescriptorUtils.findIndexedPropertyType(propertyName, this.propertyType, indexedReadMethod, indexedWriteMethod);
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
    public Method getIndexedReadMethod() {
        return this.indexedReadMethod;
    }
    
    @Override
    public void setIndexedReadMethod(final Method indexedReadMethod) throws IntrospectionException {
        this.indexedReadMethod = indexedReadMethod;
    }
    
    @Override
    public Method getIndexedWriteMethod() {
        return this.indexedWriteMethod;
    }
    
    @Override
    public void setIndexedWriteMethod(final Method indexedWriteMethod) throws IntrospectionException {
        this.indexedWriteMethod = indexedWriteMethod;
    }
    
    @Override
    public Class<?> getIndexedPropertyType() {
        if (this.indexedPropertyType == null) {
            try {
                this.indexedPropertyType = PropertyDescriptorUtils.findIndexedPropertyType(this.getName(), this.getPropertyType(), this.indexedReadMethod, this.indexedWriteMethod);
            }
            catch (IntrospectionException ex) {}
        }
        return this.indexedPropertyType;
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
        if (this == obj) {
            return true;
        }
        if (obj != null && obj instanceof IndexedPropertyDescriptor) {
            final IndexedPropertyDescriptor other = (IndexedPropertyDescriptor)obj;
            return PropertyDescriptorUtils.compareMethods(this.getIndexedReadMethod(), other.getIndexedReadMethod()) && PropertyDescriptorUtils.compareMethods(this.getIndexedWriteMethod(), other.getIndexedWriteMethod()) && this.getIndexedPropertyType() == other.getIndexedPropertyType() && PropertyDescriptorUtils.equals(this, obj);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("%s[name=%s, propertyType=%s, indexedPropertyType=%s, readMethod=%s, writeMethod=%s, indexedReadMethod=%s, indexedWriteMethod=%s]", this.getClass().getSimpleName(), this.getName(), this.getPropertyType(), this.getIndexedPropertyType(), this.readMethod, this.writeMethod, this.indexedReadMethod, this.indexedWriteMethod);
    }
}
