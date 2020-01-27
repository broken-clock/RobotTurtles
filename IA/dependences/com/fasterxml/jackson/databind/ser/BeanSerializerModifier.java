// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.JsonSerializer;
import java.util.List;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;

public abstract class BeanSerializerModifier
{
    public List<BeanPropertyWriter> changeProperties(final SerializationConfig config, final BeanDescription beanDesc, final List<BeanPropertyWriter> beanProperties) {
        return beanProperties;
    }
    
    public List<BeanPropertyWriter> orderProperties(final SerializationConfig config, final BeanDescription beanDesc, final List<BeanPropertyWriter> beanProperties) {
        return beanProperties;
    }
    
    public BeanSerializerBuilder updateBuilder(final SerializationConfig config, final BeanDescription beanDesc, final BeanSerializerBuilder builder) {
        return builder;
    }
    
    public JsonSerializer<?> modifySerializer(final SerializationConfig config, final BeanDescription beanDesc, final JsonSerializer<?> serializer) {
        return serializer;
    }
    
    public JsonSerializer<?> modifyArraySerializer(final SerializationConfig config, final ArrayType valueType, final BeanDescription beanDesc, final JsonSerializer<?> serializer) {
        return serializer;
    }
    
    public JsonSerializer<?> modifyCollectionSerializer(final SerializationConfig config, final CollectionType valueType, final BeanDescription beanDesc, final JsonSerializer<?> serializer) {
        return serializer;
    }
    
    public JsonSerializer<?> modifyCollectionLikeSerializer(final SerializationConfig config, final CollectionLikeType valueType, final BeanDescription beanDesc, final JsonSerializer<?> serializer) {
        return serializer;
    }
    
    public JsonSerializer<?> modifyMapSerializer(final SerializationConfig config, final MapType valueType, final BeanDescription beanDesc, final JsonSerializer<?> serializer) {
        return serializer;
    }
    
    public JsonSerializer<?> modifyMapLikeSerializer(final SerializationConfig config, final MapLikeType valueType, final BeanDescription beanDesc, final JsonSerializer<?> serializer) {
        return serializer;
    }
    
    public JsonSerializer<?> modifyEnumSerializer(final SerializationConfig config, final JavaType valueType, final BeanDescription beanDesc, final JsonSerializer<?> serializer) {
        return serializer;
    }
    
    public JsonSerializer<?> modifyKeySerializer(final SerializationConfig config, final JavaType valueType, final BeanDescription beanDesc, final JsonSerializer<?> serializer) {
        return serializer;
    }
}
