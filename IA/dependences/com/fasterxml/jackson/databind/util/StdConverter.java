// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

public abstract class StdConverter<IN, OUT> implements Converter<IN, OUT>
{
    @Override
    public abstract OUT convert(final IN p0);
    
    @Override
    public JavaType getInputType(final TypeFactory typeFactory) {
        return this._findConverterType(typeFactory).containedType(0);
    }
    
    @Override
    public JavaType getOutputType(final TypeFactory typeFactory) {
        return this._findConverterType(typeFactory).containedType(1);
    }
    
    protected JavaType _findConverterType(final TypeFactory tf) {
        final JavaType thisType = tf.constructType(this.getClass());
        final JavaType convType = thisType.findSuperType(Converter.class);
        if (convType == null || convType.containedTypeCount() < 2) {
            throw new IllegalStateException("Cannot find OUT type parameter for Converter of type " + this.getClass().getName());
        }
        return convType;
    }
}
