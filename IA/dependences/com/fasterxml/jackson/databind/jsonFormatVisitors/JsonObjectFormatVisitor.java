// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.BeanProperty;

public interface JsonObjectFormatVisitor extends JsonFormatVisitorWithSerializerProvider
{
    void property(final BeanProperty p0) throws JsonMappingException;
    
    void property(final String p0, final JsonFormatVisitable p1, final JavaType p2) throws JsonMappingException;
    
    void optionalProperty(final BeanProperty p0) throws JsonMappingException;
    
    void optionalProperty(final String p0, final JsonFormatVisitable p1, final JavaType p2) throws JsonMappingException;
    
    public static class Base implements JsonObjectFormatVisitor
    {
        protected SerializerProvider _provider;
        
        public Base() {
        }
        
        public Base(final SerializerProvider p) {
            this._provider = p;
        }
        
        @Override
        public SerializerProvider getProvider() {
            return this._provider;
        }
        
        @Override
        public void setProvider(final SerializerProvider p) {
            this._provider = p;
        }
        
        @Override
        public void property(final BeanProperty prop) throws JsonMappingException {
        }
        
        @Override
        public void property(final String name, final JsonFormatVisitable handler, final JavaType propertyTypeHint) throws JsonMappingException {
        }
        
        @Override
        public void optionalProperty(final BeanProperty prop) throws JsonMappingException {
        }
        
        @Override
        public void optionalProperty(final String name, final JsonFormatVisitable handler, final JavaType propertyTypeHint) throws JsonMappingException {
        }
    }
}
