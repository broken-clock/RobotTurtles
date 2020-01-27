// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.Converter;

final class NumberToCharacterConverter implements Converter<Number, Character>
{
    @Override
    public Character convert(final Number source) {
        return (char)source.shortValue();
    }
}
