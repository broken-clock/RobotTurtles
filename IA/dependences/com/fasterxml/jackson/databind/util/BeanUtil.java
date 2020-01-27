// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

public class BeanUtil
{
    public static String okNameForGetter(final AnnotatedMethod am, final boolean stdNaming) {
        final String name = am.getName();
        String str = okNameForIsGetter(am, name, stdNaming);
        if (str == null) {
            str = okNameForRegularGetter(am, name, stdNaming);
        }
        return str;
    }
    
    public static String okNameForRegularGetter(final AnnotatedMethod am, final String name, final boolean stdNaming) {
        if (name.startsWith("get")) {
            if ("getCallbacks".equals(name)) {
                if (isCglibGetCallbacks(am)) {
                    return null;
                }
            }
            else if ("getMetaClass".equals(name) && isGroovyMetaClassGetter(am)) {
                return null;
            }
            return stdNaming ? stdManglePropertyName(name, 3) : legacyManglePropertyName(name, 3);
        }
        return null;
    }
    
    public static String okNameForIsGetter(final AnnotatedMethod am, final String name, final boolean stdNaming) {
        if (name.startsWith("is")) {
            final Class<?> rt = am.getRawType();
            if (rt == Boolean.class || rt == Boolean.TYPE) {
                return stdNaming ? stdManglePropertyName(name, 2) : legacyManglePropertyName(name, 2);
            }
        }
        return null;
    }
    
    @Deprecated
    public static String okNameForSetter(final AnnotatedMethod am, final boolean stdNaming) {
        final String name = okNameForMutator(am, "set", stdNaming);
        if (name != null && (!"metaClass".equals(name) || !isGroovyMetaClassSetter(am))) {
            return name;
        }
        return null;
    }
    
    public static String okNameForMutator(final AnnotatedMethod am, final String prefix, final boolean stdNaming) {
        final String name = am.getName();
        if (name.startsWith(prefix)) {
            return stdNaming ? stdManglePropertyName(name, prefix.length()) : legacyManglePropertyName(name, prefix.length());
        }
        return null;
    }
    
    public static Object getDefaultValue(final JavaType type) {
        final Class<?> cls = type.getRawClass();
        final Class<?> prim = ClassUtil.primitiveType(cls);
        if (prim != null) {
            return ClassUtil.defaultValue(prim);
        }
        if (type.isContainerType() || type.isReferenceType()) {
            return JsonInclude.Include.NON_EMPTY;
        }
        if (cls == String.class) {
            return "";
        }
        if (type.isTypeOrSubTypeOf(Date.class)) {
            return new Date(0L);
        }
        if (type.isTypeOrSubTypeOf(Calendar.class)) {
            final Calendar c = new GregorianCalendar();
            c.setTimeInMillis(0L);
            return c;
        }
        return null;
    }
    
    protected static boolean isCglibGetCallbacks(final AnnotatedMethod am) {
        final Class<?> rt = am.getRawType();
        if (rt.isArray()) {
            final Class<?> compType = rt.getComponentType();
            final String pkgName = ClassUtil.getPackageName(compType);
            if (pkgName != null && pkgName.contains(".cglib")) {
                return pkgName.startsWith("net.sf.cglib") || pkgName.startsWith("org.hibernate.repackage.cglib") || pkgName.startsWith("org.springframework.cglib");
            }
        }
        return false;
    }
    
    protected static boolean isGroovyMetaClassSetter(final AnnotatedMethod am) {
        final Class<?> argType = am.getRawParameterType(0);
        final String pkgName = ClassUtil.getPackageName(argType);
        return pkgName != null && pkgName.startsWith("groovy.lang");
    }
    
    protected static boolean isGroovyMetaClassGetter(final AnnotatedMethod am) {
        final String pkgName = ClassUtil.getPackageName(am.getRawType());
        return pkgName != null && pkgName.startsWith("groovy.lang");
    }
    
    protected static String legacyManglePropertyName(final String basename, final int offset) {
        final int end = basename.length();
        if (end == offset) {
            return null;
        }
        char c = basename.charAt(offset);
        char d = Character.toLowerCase(c);
        if (c == d) {
            return basename.substring(offset);
        }
        final StringBuilder sb = new StringBuilder(end - offset);
        sb.append(d);
        for (int i = offset + 1; i < end; ++i) {
            c = basename.charAt(i);
            d = Character.toLowerCase(c);
            if (c == d) {
                sb.append(basename, i, end);
                break;
            }
            sb.append(d);
        }
        return sb.toString();
    }
    
    protected static String stdManglePropertyName(final String basename, final int offset) {
        final int end = basename.length();
        if (end == offset) {
            return null;
        }
        final char c0 = basename.charAt(offset);
        final char c2 = Character.toLowerCase(c0);
        if (c0 == c2) {
            return basename.substring(offset);
        }
        if (offset + 1 < end && Character.isUpperCase(basename.charAt(offset + 1))) {
            return basename.substring(offset);
        }
        final StringBuilder sb = new StringBuilder(end - offset);
        sb.append(c2);
        sb.append(basename, offset + 1, end);
        return sb.toString();
    }
}
