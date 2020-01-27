// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import org.springframework.util.StringUtils;
import java.util.UUID;
import org.springframework.core.convert.converter.Converter;

final class StringToUUIDConverter implements Converter<String, UUID>
{
    @Override
    public UUID convert(final String source) {
        if (StringUtils.hasLength(source)) {
            return UUID.fromString(source.trim());
        }
        return null;
    }
}
