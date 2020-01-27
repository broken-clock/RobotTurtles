// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationConfig;

public interface Serializers
{
    JsonSerializer<?> findSerializer(final SerializationConfig p0, final JavaType p1, final BeanDescription p2);
    
    JsonSerializer<?> findReferenceSerializer(final SerializationConfig p0, final ReferenceType p1, final BeanDescription p2, final TypeSerializer p3, final JsonSerializer<Object> p4);
    
    JsonSerializer<?> findArraySerializer(final SerializationConfig p0, final ArrayType p1, final BeanDescription p2, final TypeSerializer p3, final JsonSerializer<Object> p4);
    
    JsonSerializer<?> findCollectionSerializer(final SerializationConfig p0, final CollectionType p1, final BeanDescription p2, final TypeSerializer p3, final JsonSerializer<Object> p4);
    
    JsonSerializer<?> findCollectionLikeSerializer(final SerializationConfig p0, final CollectionLikeType p1, final BeanDescription p2, final TypeSerializer p3, final JsonSerializer<Object> p4);
    
    JsonSerializer<?> findMapSerializer(final SerializationConfig p0, final MapType p1, final BeanDescription p2, final JsonSerializer<Object> p3, final TypeSerializer p4, final JsonSerializer<Object> p5);
    
    JsonSerializer<?> findMapLikeSerializer(final SerializationConfig p0, final MapLikeType p1, final BeanDescription p2, final JsonSerializer<Object> p3, final TypeSerializer p4, final JsonSerializer<Object> p5);
    
    public static class Base implements Serializers
    {
        @Override
        public JsonSerializer<?> findSerializer(final SerializationConfig config, final JavaType type, final BeanDescription beanDesc) {
            return null;
        }
        
        @Override
        public JsonSerializer<?> findReferenceSerializer(final SerializationConfig config, final ReferenceType type, final BeanDescription beanDesc, final TypeSerializer contentTypeSerializer, final JsonSerializer<Object> contentValueSerializer) {
            return this.findSerializer(config, type, beanDesc);
        }
        
        @Override
        public JsonSerializer<?> findArraySerializer(final SerializationConfig config, final ArrayType type, final BeanDescription beanDesc, final TypeSerializer elementTypeSerializer, final JsonSerializer<Object> elementValueSerializer) {
            return null;
        }
        
        @Override
        public JsonSerializer<?> findCollectionSerializer(final SerializationConfig config, final CollectionType type, final BeanDescription beanDesc, final TypeSerializer elementTypeSerializer, final JsonSerializer<Object> elementValueSerializer) {
            return null;
        }
        
        @Override
        public JsonSerializer<?> findCollectionLikeSerializer(final SerializationConfig config, final CollectionLikeType type, final BeanDescription beanDesc, final TypeSerializer elementTypeSerializer, final JsonSerializer<Object> elementValueSerializer) {
            return null;
        }
        
        @Override
        public JsonSerializer<?> findMapSerializer(final SerializationConfig config, final MapType type, final BeanDescription beanDesc, final JsonSerializer<Object> keySerializer, final TypeSerializer elementTypeSerializer, final JsonSerializer<Object> elementValueSerializer) {
            return null;
        }
        
        @Override
        public JsonSerializer<?> findMapLikeSerializer(final SerializationConfig config, final MapLikeType type, final BeanDescription beanDesc, final JsonSerializer<Object> keySerializer, final TypeSerializer elementTypeSerializer, final JsonSerializer<Object> elementValueSerializer) {
            return null;
        }
    }
}
