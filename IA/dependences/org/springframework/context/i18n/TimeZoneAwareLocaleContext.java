// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.i18n;

import java.util.TimeZone;

public interface TimeZoneAwareLocaleContext extends LocaleContext
{
    TimeZone getTimeZone();
}
