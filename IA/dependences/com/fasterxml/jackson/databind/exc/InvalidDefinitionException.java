// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.exc;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.Closeable;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;

public class InvalidDefinitionException extends JsonMappingException
{
    protected final JavaType _type;
    protected transient BeanDescription _beanDesc;
    protected transient BeanPropertyDefinition _property;
    
    protected InvalidDefinitionException(final JsonParser p, final String msg, final JavaType type) {
        super(p, msg);
        this._type = type;
        this._beanDesc = null;
        this._property = null;
    }
    
    protected InvalidDefinitionException(final JsonGenerator g, final String msg, final JavaType type) {
        super(g, msg);
        this._type = type;
        this._beanDesc = null;
        this._property = null;
    }
    
    protected InvalidDefinitionException(final JsonParser p, final String msg, final BeanDescription bean, final BeanPropertyDefinition prop) {
        super(p, msg);
        this._type = ((bean == null) ? null : bean.getType());
        this._beanDesc = bean;
        this._property = prop;
    }
    
    protected InvalidDefinitionException(final JsonGenerator g, final String msg, final BeanDescription bean, final BeanPropertyDefinition prop) {
        super(g, msg);
        this._type = ((bean == null) ? null : bean.getType());
        this._beanDesc = bean;
        this._property = prop;
    }
    
    public static InvalidDefinitionException from(final JsonParser p, final String msg, final BeanDescription bean, final BeanPropertyDefinition prop) {
        return new InvalidDefinitionException(p, msg, bean, prop);
    }
    
    public static InvalidDefinitionException from(final JsonParser p, final String msg, final JavaType type) {
        return new InvalidDefinitionException(p, msg, type);
    }
    
    public static InvalidDefinitionException from(final JsonGenerator g, final String msg, final BeanDescription bean, final BeanPropertyDefinition prop) {
        return new InvalidDefinitionException(g, msg, bean, prop);
    }
    
    public static InvalidDefinitionException from(final JsonGenerator g, final String msg, final JavaType type) {
        return new InvalidDefinitionException(g, msg, type);
    }
    
    public JavaType getType() {
        return this._type;
    }
    
    public BeanDescription getBeanDescription() {
        return this._beanDesc;
    }
    
    public BeanPropertyDefinition getProperty() {
        return this._property;
    }
}
