// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.BeanProperty;

public class ValueInjector extends BeanProperty.Std
{
    private static final long serialVersionUID = 1L;
    protected final Object _valueId;
    
    public ValueInjector(final PropertyName propName, final JavaType type, final AnnotatedMember mutator, final Object valueId) {
        super(propName, type, null, mutator, PropertyMetadata.STD_OPTIONAL);
        this._valueId = valueId;
    }
    
    @Deprecated
    public ValueInjector(final PropertyName propName, final JavaType type, final Annotations contextAnnotations, final AnnotatedMember mutator, final Object valueId) {
        this(propName, type, mutator, valueId);
    }
    
    public Object findValue(final DeserializationContext context, final Object beanInstance) throws JsonMappingException {
        return context.findInjectableValue(this._valueId, this, beanInstance);
    }
    
    public void inject(final DeserializationContext context, final Object beanInstance) throws IOException {
        this._member.setValue(beanInstance, this.findValue(context, beanInstance));
    }
}
