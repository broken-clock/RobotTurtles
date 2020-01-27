// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.impl;

import java.io.Serializable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.BeanPropertyFilter;

public class SimpleBeanPropertyFilter implements BeanPropertyFilter, PropertyFilter
{
    protected SimpleBeanPropertyFilter() {
    }
    
    public static SimpleBeanPropertyFilter serializeAll() {
        return SerializeExceptFilter.INCLUDE_ALL;
    }
    
    @Deprecated
    public static SimpleBeanPropertyFilter serializeAll(final Set<String> properties) {
        return new FilterExceptFilter(properties);
    }
    
    public static SimpleBeanPropertyFilter filterOutAllExcept(final Set<String> properties) {
        return new FilterExceptFilter(properties);
    }
    
    public static SimpleBeanPropertyFilter filterOutAllExcept(final String... propertyArray) {
        final HashSet<String> properties = new HashSet<String>(propertyArray.length);
        Collections.addAll(properties, propertyArray);
        return new FilterExceptFilter(properties);
    }
    
    public static SimpleBeanPropertyFilter serializeAllExcept(final Set<String> properties) {
        return new SerializeExceptFilter(properties);
    }
    
    public static SimpleBeanPropertyFilter serializeAllExcept(final String... propertyArray) {
        final HashSet<String> properties = new HashSet<String>(propertyArray.length);
        Collections.addAll(properties, propertyArray);
        return new SerializeExceptFilter(properties);
    }
    
    public static PropertyFilter from(final BeanPropertyFilter src) {
        return new PropertyFilter() {
            @Override
            public void serializeAsField(final Object pojo, final JsonGenerator jgen, final SerializerProvider prov, final PropertyWriter writer) throws Exception {
                src.serializeAsField(pojo, jgen, prov, (BeanPropertyWriter)writer);
            }
            
            @Override
            public void depositSchemaProperty(final PropertyWriter writer, final ObjectNode propertiesNode, final SerializerProvider provider) throws JsonMappingException {
                src.depositSchemaProperty((BeanPropertyWriter)writer, propertiesNode, provider);
            }
            
            @Override
            public void depositSchemaProperty(final PropertyWriter writer, final JsonObjectFormatVisitor objectVisitor, final SerializerProvider provider) throws JsonMappingException {
                src.depositSchemaProperty((BeanPropertyWriter)writer, objectVisitor, provider);
            }
            
            @Override
            public void serializeAsElement(final Object elementValue, final JsonGenerator jgen, final SerializerProvider prov, final PropertyWriter writer) throws Exception {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    protected boolean include(final BeanPropertyWriter writer) {
        return true;
    }
    
    protected boolean include(final PropertyWriter writer) {
        return true;
    }
    
    protected boolean includeElement(final Object elementValue) {
        return true;
    }
    
    @Deprecated
    @Override
    public void serializeAsField(final Object bean, final JsonGenerator jgen, final SerializerProvider provider, final BeanPropertyWriter writer) throws Exception {
        if (this.include(writer)) {
            writer.serializeAsField(bean, jgen, provider);
        }
        else if (!jgen.canOmitFields()) {
            writer.serializeAsOmittedField(bean, jgen, provider);
        }
    }
    
    @Deprecated
    @Override
    public void depositSchemaProperty(final BeanPropertyWriter writer, final ObjectNode propertiesNode, final SerializerProvider provider) throws JsonMappingException {
        if (this.include(writer)) {
            writer.depositSchemaProperty(propertiesNode, provider);
        }
    }
    
    @Deprecated
    @Override
    public void depositSchemaProperty(final BeanPropertyWriter writer, final JsonObjectFormatVisitor objectVisitor, final SerializerProvider provider) throws JsonMappingException {
        if (this.include(writer)) {
            writer.depositSchemaProperty(objectVisitor, provider);
        }
    }
    
    @Override
    public void serializeAsField(final Object pojo, final JsonGenerator jgen, final SerializerProvider provider, final PropertyWriter writer) throws Exception {
        if (this.include(writer)) {
            writer.serializeAsField(pojo, jgen, provider);
        }
        else if (!jgen.canOmitFields()) {
            writer.serializeAsOmittedField(pojo, jgen, provider);
        }
    }
    
    @Override
    public void serializeAsElement(final Object elementValue, final JsonGenerator jgen, final SerializerProvider provider, final PropertyWriter writer) throws Exception {
        if (this.includeElement(elementValue)) {
            writer.serializeAsElement(elementValue, jgen, provider);
        }
    }
    
    @Deprecated
    @Override
    public void depositSchemaProperty(final PropertyWriter writer, final ObjectNode propertiesNode, final SerializerProvider provider) throws JsonMappingException {
        if (this.include(writer)) {
            writer.depositSchemaProperty(propertiesNode, provider);
        }
    }
    
    @Override
    public void depositSchemaProperty(final PropertyWriter writer, final JsonObjectFormatVisitor objectVisitor, final SerializerProvider provider) throws JsonMappingException {
        if (this.include(writer)) {
            writer.depositSchemaProperty(objectVisitor, provider);
        }
    }
    
    public static class FilterExceptFilter extends SimpleBeanPropertyFilter implements Serializable
    {
        private static final long serialVersionUID = 1L;
        protected final Set<String> _propertiesToInclude;
        
        public FilterExceptFilter(final Set<String> properties) {
            this._propertiesToInclude = properties;
        }
        
        @Override
        protected boolean include(final BeanPropertyWriter writer) {
            return this._propertiesToInclude.contains(writer.getName());
        }
        
        @Override
        protected boolean include(final PropertyWriter writer) {
            return this._propertiesToInclude.contains(writer.getName());
        }
    }
    
    public static class SerializeExceptFilter extends SimpleBeanPropertyFilter implements Serializable
    {
        private static final long serialVersionUID = 1L;
        static final SerializeExceptFilter INCLUDE_ALL;
        protected final Set<String> _propertiesToExclude;
        
        SerializeExceptFilter() {
            this._propertiesToExclude = Collections.emptySet();
        }
        
        public SerializeExceptFilter(final Set<String> properties) {
            this._propertiesToExclude = properties;
        }
        
        @Override
        protected boolean include(final BeanPropertyWriter writer) {
            return !this._propertiesToExclude.contains(writer.getName());
        }
        
        @Override
        protected boolean include(final PropertyWriter writer) {
            return !this._propertiesToExclude.contains(writer.getName());
        }
        
        static {
            INCLUDE_ALL = new SerializeExceptFilter();
        }
    }
}
