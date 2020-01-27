// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.Converter;

final class ObjectToStringConverter implements Converter<Object, String>
{
    @Override
    public String convert(final Object source) {
        return source.toString();
    }
}
