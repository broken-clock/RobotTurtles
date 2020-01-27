// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Collection;

public abstract class GenericCollectionTypeResolver
{
    public static Class<?> getCollectionType(final Class<? extends Collection> collectionClass) {
        return ResolvableType.forClass(collectionClass).asCollection().resolveGeneric(new int[0]);
    }
    
    public static Class<?> getMapKeyType(final Class<? extends Map> mapClass) {
        return ResolvableType.forClass(mapClass).asMap().resolveGeneric(0);
    }
    
    public static Class<?> getMapValueType(final Class<? extends Map> mapClass) {
        return ResolvableType.forClass(mapClass).asMap().resolveGeneric(1);
    }
    
    public static Class<?> getCollectionFieldType(final Field collectionField) {
        return ResolvableType.forField(collectionField).asCollection().resolveGeneric(new int[0]);
    }
    
    public static Class<?> getCollectionFieldType(final Field collectionField, final int nestingLevel) {
        return ResolvableType.forField(collectionField).getNested(nestingLevel).asCollection().resolveGeneric(new int[0]);
    }
    
    @Deprecated
    public static Class<?> getCollectionFieldType(final Field collectionField, final int nestingLevel, final Map<Integer, Integer> typeIndexesPerLevel) {
        return ResolvableType.forField(collectionField).getNested(nestingLevel, typeIndexesPerLevel).asCollection().resolveGeneric(new int[0]);
    }
    
    public static Class<?> getMapKeyFieldType(final Field mapField) {
        return ResolvableType.forField(mapField).asMap().resolveGeneric(0);
    }
    
    public static Class<?> getMapKeyFieldType(final Field mapField, final int nestingLevel) {
        return ResolvableType.forField(mapField).getNested(nestingLevel).asMap().resolveGeneric(0);
    }
    
    @Deprecated
    public static Class<?> getMapKeyFieldType(final Field mapField, final int nestingLevel, final Map<Integer, Integer> typeIndexesPerLevel) {
        return ResolvableType.forField(mapField).getNested(nestingLevel, typeIndexesPerLevel).asMap().resolveGeneric(0);
    }
    
    public static Class<?> getMapValueFieldType(final Field mapField) {
        return ResolvableType.forField(mapField).asMap().resolveGeneric(1);
    }
    
    public static Class<?> getMapValueFieldType(final Field mapField, final int nestingLevel) {
        return ResolvableType.forField(mapField).getNested(nestingLevel).asMap().resolveGeneric(1);
    }
    
    @Deprecated
    public static Class<?> getMapValueFieldType(final Field mapField, final int nestingLevel, final Map<Integer, Integer> typeIndexesPerLevel) {
        return ResolvableType.forField(mapField).getNested(nestingLevel, typeIndexesPerLevel).asMap().resolveGeneric(1);
    }
    
    public static Class<?> getCollectionParameterType(final MethodParameter methodParam) {
        return ResolvableType.forMethodParameter(methodParam).asCollection().resolveGeneric(new int[0]);
    }
    
    public static Class<?> getMapKeyParameterType(final MethodParameter methodParam) {
        return ResolvableType.forMethodParameter(methodParam).asMap().resolveGeneric(0);
    }
    
    public static Class<?> getMapValueParameterType(final MethodParameter methodParam) {
        return ResolvableType.forMethodParameter(methodParam).asMap().resolveGeneric(1);
    }
    
    public static Class<?> getCollectionReturnType(final Method method) {
        return ResolvableType.forMethodReturnType(method).asCollection().resolveGeneric(new int[0]);
    }
    
    public static Class<?> getCollectionReturnType(final Method method, final int nestingLevel) {
        return ResolvableType.forMethodReturnType(method).getNested(nestingLevel).asCollection().resolveGeneric(new int[0]);
    }
    
    public static Class<?> getMapKeyReturnType(final Method method) {
        return ResolvableType.forMethodReturnType(method).asMap().resolveGeneric(0);
    }
    
    public static Class<?> getMapKeyReturnType(final Method method, final int nestingLevel) {
        return ResolvableType.forMethodReturnType(method).getNested(nestingLevel).asMap().resolveGeneric(0);
    }
    
    public static Class<?> getMapValueReturnType(final Method method) {
        return ResolvableType.forMethodReturnType(method).asMap().resolveGeneric(1);
    }
    
    public static Class<?> getMapValueReturnType(final Method method, final int nestingLevel) {
        return ResolvableType.forMethodReturnType(method).getNested(nestingLevel).asMap().resolveGeneric(1);
    }
}
