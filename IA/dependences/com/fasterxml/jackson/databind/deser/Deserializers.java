// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;

public interface Deserializers
{
    JsonDeserializer<?> findEnumDeserializer(final Class<?> p0, final DeserializationConfig p1, final BeanDescription p2) throws JsonMappingException;
    
    JsonDeserializer<?> findTreeNodeDeserializer(final Class<? extends JsonNode> p0, final DeserializationConfig p1, final BeanDescription p2) throws JsonMappingException;
    
    JsonDeserializer<?> findBeanDeserializer(final JavaType p0, final DeserializationConfig p1, final BeanDescription p2) throws JsonMappingException;
    
    JsonDeserializer<?> findReferenceDeserializer(final ReferenceType p0, final DeserializationConfig p1, final BeanDescription p2, final TypeDeserializer p3, final JsonDeserializer<?> p4) throws JsonMappingException;
    
    JsonDeserializer<?> findArrayDeserializer(final ArrayType p0, final DeserializationConfig p1, final BeanDescription p2, final TypeDeserializer p3, final JsonDeserializer<?> p4) throws JsonMappingException;
    
    JsonDeserializer<?> findCollectionDeserializer(final CollectionType p0, final DeserializationConfig p1, final BeanDescription p2, final TypeDeserializer p3, final JsonDeserializer<?> p4) throws JsonMappingException;
    
    JsonDeserializer<?> findCollectionLikeDeserializer(final CollectionLikeType p0, final DeserializationConfig p1, final BeanDescription p2, final TypeDeserializer p3, final JsonDeserializer<?> p4) throws JsonMappingException;
    
    JsonDeserializer<?> findMapDeserializer(final MapType p0, final DeserializationConfig p1, final BeanDescription p2, final KeyDeserializer p3, final TypeDeserializer p4, final JsonDeserializer<?> p5) throws JsonMappingException;
    
    JsonDeserializer<?> findMapLikeDeserializer(final MapLikeType p0, final DeserializationConfig p1, final BeanDescription p2, final KeyDeserializer p3, final TypeDeserializer p4, final JsonDeserializer<?> p5) throws JsonMappingException;
    
    public static class Base implements Deserializers
    {
        @Override
        public JsonDeserializer<?> findEnumDeserializer(final Class<?> type, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonDeserializer<?> findTreeNodeDeserializer(final Class<? extends JsonNode> nodeType, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonDeserializer<?> findReferenceDeserializer(final ReferenceType refType, final DeserializationConfig config, final BeanDescription beanDesc, final TypeDeserializer contentTypeDeserializer, final JsonDeserializer<?> contentDeserializer) throws JsonMappingException {
            return this.findBeanDeserializer(refType, config, beanDesc);
        }
        
        @Override
        public JsonDeserializer<?> findBeanDeserializer(final JavaType type, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonDeserializer<?> findArrayDeserializer(final ArrayType type, final DeserializationConfig config, final BeanDescription beanDesc, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonDeserializer<?> findCollectionDeserializer(final CollectionType type, final DeserializationConfig config, final BeanDescription beanDesc, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonDeserializer<?> findCollectionLikeDeserializer(final CollectionLikeType type, final DeserializationConfig config, final BeanDescription beanDesc, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonDeserializer<?> findMapDeserializer(final MapType type, final DeserializationConfig config, final BeanDescription beanDesc, final KeyDeserializer keyDeserializer, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonDeserializer<?> findMapLikeDeserializer(final MapLikeType type, final DeserializationConfig config, final BeanDescription beanDesc, final KeyDeserializer keyDeserializer, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
            return null;
        }
    }
}
