// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import java.lang.annotation.Annotation;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.PropertyMetadata;
import java.io.Serializable;
import com.fasterxml.jackson.databind.introspect.ConcreteBeanPropertyBase;

public abstract class PropertyWriter extends ConcreteBeanPropertyBase implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    protected PropertyWriter(final PropertyMetadata md) {
        super(md);
    }
    
    protected PropertyWriter(final BeanPropertyDefinition propDef) {
        super(propDef.getMetadata());
    }
    
    protected PropertyWriter(final PropertyWriter base) {
        super(base);
    }
    
    @Override
    public abstract String getName();
    
    @Override
    public abstract PropertyName getFullName();
    
    public <A extends Annotation> A findAnnotation(final Class<A> acls) {
        A ann = (A)this.getAnnotation((Class<Annotation>)acls);
        if (ann == null) {
            ann = (A)this.getContextAnnotation((Class<Annotation>)acls);
        }
        return ann;
    }
    
    @Override
    public abstract <A extends Annotation> A getAnnotation(final Class<A> p0);
    
    @Override
    public abstract <A extends Annotation> A getContextAnnotation(final Class<A> p0);
    
    public abstract void serializeAsField(final Object p0, final JsonGenerator p1, final SerializerProvider p2) throws Exception;
    
    public abstract void serializeAsOmittedField(final Object p0, final JsonGenerator p1, final SerializerProvider p2) throws Exception;
    
    public abstract void serializeAsElement(final Object p0, final JsonGenerator p1, final SerializerProvider p2) throws Exception;
    
    public abstract void serializeAsPlaceholder(final Object p0, final JsonGenerator p1, final SerializerProvider p2) throws Exception;
    
    @Override
    public abstract void depositSchemaProperty(final JsonObjectFormatVisitor p0, final SerializerProvider p1) throws JsonMappingException;
    
    @Deprecated
    public abstract void depositSchemaProperty(final ObjectNode p0, final SerializerProvider p1) throws JsonMappingException;
}
