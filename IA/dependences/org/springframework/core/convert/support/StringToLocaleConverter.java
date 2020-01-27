// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import org.springframework.util.StringUtils;
import java.util.Locale;
import org.springframework.core.convert.converter.Converter;

final class StringToLocaleConverter implements Converter<String, Locale>
{
    @Override
    public Locale convert(final String source) {
        return StringUtils.parseLocaleString(source);
    }
}
