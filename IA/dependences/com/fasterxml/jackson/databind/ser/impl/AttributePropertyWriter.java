// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter;

public class AttributePropertyWriter extends VirtualBeanPropertyWriter
{
    private static final long serialVersionUID = 1L;
    protected final String _attrName;
    
    protected AttributePropertyWriter(final String attrName, final BeanPropertyDefinition propDef, final Annotations contextAnnotations, final JavaType declaredType) {
        this(attrName, propDef, contextAnnotations, declaredType, propDef.findInclusion());
    }
    
    protected AttributePropertyWriter(final String attrName, final BeanPropertyDefinition propDef, final Annotations contextAnnotations, final JavaType declaredType, final JsonInclude.Value inclusion) {
        super(propDef, contextAnnotations, declaredType, null, null, null, inclusion, null);
        this._attrName = attrName;
    }
    
    public static AttributePropertyWriter construct(final String attrName, final BeanPropertyDefinition propDef, final Annotations contextAnnotations, final JavaType declaredType) {
        return new AttributePropertyWriter(attrName, propDef, contextAnnotations, declaredType);
    }
    
    protected AttributePropertyWriter(final AttributePropertyWriter base) {
        super(base);
        this._attrName = base._attrName;
    }
    
    @Override
    public VirtualBeanPropertyWriter withConfig(final MapperConfig<?> config, final AnnotatedClass declaringClass, final BeanPropertyDefinition propDef, final JavaType type) {
        throw new IllegalStateException("Should not be called on this type");
    }
    
    @Override
    protected Object value(final Object bean, final JsonGenerator jgen, final SerializerProvider prov) throws Exception {
        return prov.getAttribute(this._attrName);
    }
}
