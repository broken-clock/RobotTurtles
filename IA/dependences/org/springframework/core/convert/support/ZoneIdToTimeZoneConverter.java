// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import java.util.TimeZone;
import java.time.ZoneId;
import org.springframework.core.convert.converter.Converter;

final class ZoneIdToTimeZoneConverter implements Converter<ZoneId, TimeZone>
{
    @Override
    public TimeZone convert(final ZoneId source) {
        return TimeZone.getTimeZone(source);
    }
}
