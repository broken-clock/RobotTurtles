// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import java.lang.reflect.Method;
import java.beans.IntrospectionException;
import java.util.Enumeration;
import java.beans.PropertyDescriptor;

class PropertyDescriptorUtils
{
    public static void copyNonMethodProperties(final PropertyDescriptor source, final PropertyDescriptor target) throws IntrospectionException {
        target.setExpert(source.isExpert());
        target.setHidden(source.isHidden());
        target.setPreferred(source.isPreferred());
        target.setName(source.getName());
        target.setShortDescription(source.getShortDescription());
        target.setDisplayName(source.getDisplayName());
        final Enumeration<String> keys = source.attributeNames();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            target.setValue(key, source.getValue(key));
        }
        target.setPropertyEditorClass(source.getPropertyEditorClass());
        target.setBound(source.isBound());
        target.setConstrained(source.isConstrained());
    }
    
    public static Class<?> findPropertyType(final Method readMethod, final Method writeMethod) throws IntrospectionException {
        Class<?> propertyType = null;
        if (readMethod != null) {
            final Class<?>[] params = readMethod.getParameterTypes();
            if (params.length != 0) {
                throw new IntrospectionException("Bad read method arg count: " + readMethod);
            }
            propertyType = readMethod.getReturnType();
            if (propertyType == Void.TYPE) {
                throw new IntrospectionException("Read method returns void: " + readMethod);
            }
        }
        if (writeMethod != null) {
            final Class<?>[] params = writeMethod.getParameterTypes();
            if (params.length != 1) {
                throw new IntrospectionException("Bad write method arg count: " + writeMethod);
            }
            if (propertyType != null) {
                if (propertyType.isAssignableFrom(params[0])) {
                    propertyType = params[0];
                }
                else if (!params[0].isAssignableFrom(propertyType)) {
                    throw new IntrospectionException("Type mismatch between read and write methods: " + readMethod + " - " + writeMethod);
                }
            }
            else {
                propertyType = params[0];
            }
        }
        return propertyType;
    }
    
    public static Class<?> findIndexedPropertyType(final String name, final Class<?> propertyType, final Method indexedReadMethod, final Method indexedWriteMethod) throws IntrospectionException {
        Class<?> indexedPropertyType = null;
        if (indexedReadMethod != null) {
            final Class<?>[] params = indexedReadMethod.getParameterTypes();
            if (params.length != 1) {
                throw new IntrospectionException("Bad indexed read method arg count: " + indexedReadMethod);
            }
            if (params[0] != Integer.TYPE) {
                throw new IntrospectionException("Non int index to indexed read method: " + indexedReadMethod);
            }
            indexedPropertyType = indexedReadMethod.getReturnType();
            if (indexedPropertyType == Void.TYPE) {
                throw new IntrospectionException("Indexed read method returns void: " + indexedReadMethod);
            }
        }
        if (indexedWriteMethod != null) {
            final Class<?>[] params = indexedWriteMethod.getParameterTypes();
            if (params.length != 2) {
                throw new IntrospectionException("Bad indexed write method arg count: " + indexedWriteMethod);
            }
            if (params[0] != Integer.TYPE) {
                throw new IntrospectionException("Non int index to indexed write method: " + indexedWriteMethod);
            }
            if (indexedPropertyType != null) {
                if (indexedPropertyType.isAssignableFrom(params[1])) {
                    indexedPropertyType = params[1];
                }
                else if (!params[1].isAssignableFrom(indexedPropertyType)) {
                    throw new IntrospectionException("Type mismatch between indexed read and write methods: " + indexedReadMethod + " - " + indexedWriteMethod);
                }
            }
            else {
                indexedPropertyType = params[1];
            }
        }
        if (propertyType != null && (!propertyType.isArray() || propertyType.getComponentType() != indexedPropertyType)) {
            throw new IntrospectionException("Type mismatch between indexed and non-indexed methods: " + indexedReadMethod + " - " + indexedWriteMethod);
        }
        return indexedPropertyType;
    }
    
    public static boolean equals(final PropertyDescriptor pd1, final Object obj) {
        if (pd1 == obj) {
            return true;
        }
        if (obj != null && obj instanceof PropertyDescriptor) {
            final PropertyDescriptor pd2 = (PropertyDescriptor)obj;
            if (!compareMethods(pd1.getReadMethod(), pd2.getReadMethod())) {
                return false;
            }
            if (!compareMethods(pd1.getWriteMethod(), pd2.getWriteMethod())) {
                return false;
            }
            if (pd1.getPropertyType() == pd2.getPropertyType() && pd1.getPropertyEditorClass() == pd2.getPropertyEditorClass() && pd1.isBound() == pd2.isBound() && pd1.isConstrained() == pd2.isConstrained()) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean compareMethods(final Method a, final Method b) {
        return a == null == (b == null) && (a == null || a.equals(b));
    }
}
