// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.i18n;

import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.core.NamedThreadLocal;
import java.util.TimeZone;
import java.util.Locale;

public abstract class LocaleContextHolder
{
    private static final ThreadLocal<LocaleContext> localeContextHolder;
    private static final ThreadLocal<LocaleContext> inheritableLocaleContextHolder;
    
    public static void resetLocaleContext() {
        LocaleContextHolder.localeContextHolder.remove();
        LocaleContextHolder.inheritableLocaleContextHolder.remove();
    }
    
    public static void setLocaleContext(final LocaleContext localeContext) {
        setLocaleContext(localeContext, false);
    }
    
    public static void setLocaleContext(final LocaleContext localeContext, final boolean inheritable) {
        if (localeContext == null) {
            resetLocaleContext();
        }
        else if (inheritable) {
            LocaleContextHolder.inheritableLocaleContextHolder.set(localeContext);
            LocaleContextHolder.localeContextHolder.remove();
        }
        else {
            LocaleContextHolder.localeContextHolder.set(localeContext);
            LocaleContextHolder.inheritableLocaleContextHolder.remove();
        }
    }
    
    public static LocaleContext getLocaleContext() {
        LocaleContext localeContext = LocaleContextHolder.localeContextHolder.get();
        if (localeContext == null) {
            localeContext = LocaleContextHolder.inheritableLocaleContextHolder.get();
        }
        return localeContext;
    }
    
    public static void setLocale(final Locale locale) {
        setLocale(locale, false);
    }
    
    public static void setLocale(final Locale locale, final boolean inheritable) {
        LocaleContext localeContext = getLocaleContext();
        final TimeZone timeZone = (localeContext instanceof TimeZoneAwareLocaleContext) ? ((TimeZoneAwareLocaleContext)localeContext).getTimeZone() : null;
        if (timeZone != null) {
            localeContext = new SimpleTimeZoneAwareLocaleContext(locale, timeZone);
        }
        else if (locale != null) {
            localeContext = new SimpleLocaleContext(locale);
        }
        else {
            localeContext = null;
        }
        setLocaleContext(localeContext, inheritable);
    }
    
    public static Locale getLocale() {
        final LocaleContext localeContext = getLocaleContext();
        if (localeContext != null) {
            final Locale locale = localeContext.getLocale();
            if (locale != null) {
                return locale;
            }
        }
        return Locale.getDefault();
    }
    
    public static void setTimeZone(final TimeZone timeZone) {
        setTimeZone(timeZone, false);
    }
    
    public static void setTimeZone(final TimeZone timeZone, final boolean inheritable) {
        LocaleContext localeContext = getLocaleContext();
        final Locale locale = (localeContext != null) ? localeContext.getLocale() : null;
        if (timeZone != null) {
            localeContext = new SimpleTimeZoneAwareLocaleContext(locale, timeZone);
        }
        else if (locale != null) {
            localeContext = new SimpleLocaleContext(locale);
        }
        else {
            localeContext = null;
        }
        setLocaleContext(localeContext, inheritable);
    }
    
    public static TimeZone getTimeZone() {
        final LocaleContext localeContext = getLocaleContext();
        if (localeContext instanceof TimeZoneAwareLocaleContext) {
            final TimeZone timeZone = ((TimeZoneAwareLocaleContext)localeContext).getTimeZone();
            if (timeZone != null) {
                return timeZone;
            }
        }
        return TimeZone.getDefault();
    }
    
    static {
        localeContextHolder = new NamedThreadLocal<LocaleContext>("Locale context");
        inheritableLocaleContextHolder = new NamedInheritableThreadLocal<LocaleContext>("Locale context");
    }
}
