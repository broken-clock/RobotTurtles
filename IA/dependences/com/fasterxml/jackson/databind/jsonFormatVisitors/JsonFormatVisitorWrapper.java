// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JavaType;

public interface JsonFormatVisitorWrapper extends JsonFormatVisitorWithSerializerProvider
{
    JsonObjectFormatVisitor expectObjectFormat(final JavaType p0) throws JsonMappingException;
    
    JsonArrayFormatVisitor expectArrayFormat(final JavaType p0) throws JsonMappingException;
    
    JsonStringFormatVisitor expectStringFormat(final JavaType p0) throws JsonMappingException;
    
    JsonNumberFormatVisitor expectNumberFormat(final JavaType p0) throws JsonMappingException;
    
    JsonIntegerFormatVisitor expectIntegerFormat(final JavaType p0) throws JsonMappingException;
    
    JsonBooleanFormatVisitor expectBooleanFormat(final JavaType p0) throws JsonMappingException;
    
    JsonNullFormatVisitor expectNullFormat(final JavaType p0) throws JsonMappingException;
    
    JsonAnyFormatVisitor expectAnyFormat(final JavaType p0) throws JsonMappingException;
    
    JsonMapFormatVisitor expectMapFormat(final JavaType p0) throws JsonMappingException;
    
    public static class Base implements JsonFormatVisitorWrapper
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
        public JsonObjectFormatVisitor expectObjectFormat(final JavaType type) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonArrayFormatVisitor expectArrayFormat(final JavaType type) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonStringFormatVisitor expectStringFormat(final JavaType type) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonNumberFormatVisitor expectNumberFormat(final JavaType type) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonIntegerFormatVisitor expectIntegerFormat(final JavaType type) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonBooleanFormatVisitor expectBooleanFormat(final JavaType type) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonNullFormatVisitor expectNullFormat(final JavaType type) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonAnyFormatVisitor expectAnyFormat(final JavaType type) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonMapFormatVisitor expectMapFormat(final JavaType type) throws JsonMappingException {
            return null;
        }
    }
}
