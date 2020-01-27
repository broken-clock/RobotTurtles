// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.util.Locale;
import java.lang.reflect.Field;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.Assert;
import java.util.HashMap;
import java.util.Map;

public class Constants
{
    private final String className;
    private final Map<String, Object> fieldCache;
    
    public Constants(final Class<?> clazz) {
        this.fieldCache = new HashMap<String, Object>();
        Assert.notNull(clazz);
        this.className = clazz.getName();
        final Field[] fields2;
        final Field[] fields = fields2 = clazz.getFields();
        for (final Field field : fields2) {
            if (ReflectionUtils.isPublicStaticFinal(field)) {
                final String name = field.getName();
                try {
                    final Object value = field.get(null);
                    this.fieldCache.put(name, value);
                }
                catch (IllegalAccessException ex) {}
            }
        }
    }
    
    public final String getClassName() {
        return this.className;
    }
    
    public final int getSize() {
        return this.fieldCache.size();
    }
    
    protected final Map<String, Object> getFieldCache() {
        return this.fieldCache;
    }
    
    public Number asNumber(final String code) throws ConstantException {
        final Object obj = this.asObject(code);
        if (!(obj instanceof Number)) {
            throw new ConstantException(this.className, code, "not a Number");
        }
        return (Number)obj;
    }
    
    public String asString(final String code) throws ConstantException {
        return this.asObject(code).toString();
    }
    
    public Object asObject(final String code) throws ConstantException {
        Assert.notNull(code, "Code must not be null");
        final String codeToUse = code.toUpperCase(Locale.ENGLISH);
        final Object val = this.fieldCache.get(codeToUse);
        if (val == null) {
            throw new ConstantException(this.className, codeToUse, "not found");
        }
        return val;
    }
    
    public Set<String> getNames(final String namePrefix) {
        final String prefixToUse = (namePrefix != null) ? namePrefix.trim().toUpperCase(Locale.ENGLISH) : "";
        final Set<String> names = new HashSet<String>();
        for (final String code : this.fieldCache.keySet()) {
            if (code.startsWith(prefixToUse)) {
                names.add(code);
            }
        }
        return names;
    }
    
    public Set<String> getNamesForProperty(final String propertyName) {
        return this.getNames(this.propertyToConstantNamePrefix(propertyName));
    }
    
    public Set<String> getNamesForSuffix(final String nameSuffix) {
        final String suffixToUse = (nameSuffix != null) ? nameSuffix.trim().toUpperCase(Locale.ENGLISH) : "";
        final Set<String> names = new HashSet<String>();
        for (final String code : this.fieldCache.keySet()) {
            if (code.endsWith(suffixToUse)) {
                names.add(code);
            }
        }
        return names;
    }
    
    public Set<Object> getValues(final String namePrefix) {
        final String prefixToUse = (namePrefix != null) ? namePrefix.trim().toUpperCase(Locale.ENGLISH) : "";
        final Set<Object> values = new HashSet<Object>();
        for (final String code : this.fieldCache.keySet()) {
            if (code.startsWith(prefixToUse)) {
                values.add(this.fieldCache.get(code));
            }
        }
        return values;
    }
    
    public Set<Object> getValuesForProperty(final String propertyName) {
        return this.getValues(this.propertyToConstantNamePrefix(propertyName));
    }
    
    public Set<Object> getValuesForSuffix(final String nameSuffix) {
        final String suffixToUse = (nameSuffix != null) ? nameSuffix.trim().toUpperCase(Locale.ENGLISH) : "";
        final Set<Object> values = new HashSet<Object>();
        for (final String code : this.fieldCache.keySet()) {
            if (code.endsWith(suffixToUse)) {
                values.add(this.fieldCache.get(code));
            }
        }
        return values;
    }
    
    public String toCode(final Object value, final String namePrefix) throws ConstantException {
        final String prefixToUse = (namePrefix != null) ? namePrefix.trim().toUpperCase(Locale.ENGLISH) : "";
        for (final Map.Entry<String, Object> entry : this.fieldCache.entrySet()) {
            if (entry.getKey().startsWith(prefixToUse) && entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        throw new ConstantException(this.className, prefixToUse, value);
    }
    
    public String toCodeForProperty(final Object value, final String propertyName) throws ConstantException {
        return this.toCode(value, this.propertyToConstantNamePrefix(propertyName));
    }
    
    public String toCodeForSuffix(final Object value, final String nameSuffix) throws ConstantException {
        final String suffixToUse = (nameSuffix != null) ? nameSuffix.trim().toUpperCase(Locale.ENGLISH) : "";
        for (final Map.Entry<String, Object> entry : this.fieldCache.entrySet()) {
            if (entry.getKey().endsWith(suffixToUse) && entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        throw new ConstantException(this.className, suffixToUse, value);
    }
    
    public String propertyToConstantNamePrefix(final String propertyName) {
        final StringBuilder parsedPrefix = new StringBuilder();
        for (int i = 0; i < propertyName.length(); ++i) {
            final char c = propertyName.charAt(i);
            if (Character.isUpperCase(c)) {
                parsedPrefix.append("_");
                parsedPrefix.append(c);
            }
            else {
                parsedPrefix.append(Character.toUpperCase(c));
            }
        }
        return parsedPrefix.toString();
    }
}
