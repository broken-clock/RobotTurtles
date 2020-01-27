// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Collection;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import java.util.EnumMap;
import com.fasterxml.jackson.core.SerializableString;
import java.io.Serializable;

public final class EnumValues implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final Class<Enum<?>> _enumClass;
    private final Enum<?>[] _values;
    private final SerializableString[] _textual;
    private transient EnumMap<?, SerializableString> _asMap;
    
    private EnumValues(final Class<Enum<?>> enumClass, final SerializableString[] textual) {
        this._enumClass = enumClass;
        this._values = enumClass.getEnumConstants();
        this._textual = textual;
    }
    
    public static EnumValues construct(final SerializationConfig config, final Class<Enum<?>> enumClass) {
        if (config.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
            return constructFromToString(config, enumClass);
        }
        return constructFromName(config, enumClass);
    }
    
    public static EnumValues constructFromName(final MapperConfig<?> config, final Class<Enum<?>> enumClass) {
        final Class<? extends Enum<?>> enumCls = ClassUtil.findEnumType(enumClass);
        final Enum<?>[] enumValues = (Enum<?>[])enumCls.getEnumConstants();
        if (enumValues == null) {
            throw new IllegalArgumentException("Cannot determine enum constants for Class " + enumClass.getName());
        }
        final String[] names = config.getAnnotationIntrospector().findEnumValues(enumCls, enumValues, new String[enumValues.length]);
        final SerializableString[] textual = new SerializableString[enumValues.length];
        for (int i = 0, len = enumValues.length; i < len; ++i) {
            final Enum<?> en = enumValues[i];
            String name = names[i];
            if (name == null) {
                name = en.name();
            }
            textual[en.ordinal()] = config.compileString(name);
        }
        return new EnumValues(enumClass, textual);
    }
    
    public static EnumValues constructFromToString(final MapperConfig<?> config, final Class<Enum<?>> enumClass) {
        final Class<? extends Enum<?>> cls = ClassUtil.findEnumType(enumClass);
        final Enum<?>[] values = (Enum<?>[])cls.getEnumConstants();
        if (values != null) {
            final SerializableString[] textual = new SerializableString[values.length];
            for (final Enum<?> en : values) {
                textual[en.ordinal()] = config.compileString(en.toString());
            }
            return new EnumValues(enumClass, textual);
        }
        throw new IllegalArgumentException("Cannot determine enum constants for Class " + enumClass.getName());
    }
    
    public SerializableString serializedValueFor(final Enum<?> key) {
        return this._textual[key.ordinal()];
    }
    
    public Collection<SerializableString> values() {
        return Arrays.asList(this._textual);
    }
    
    public List<Enum<?>> enums() {
        return Arrays.asList(this._values);
    }
    
    public EnumMap<?, SerializableString> internalMap() {
        EnumMap<?, SerializableString> result = this._asMap;
        if (result == null) {
            final Map<Enum<?>, SerializableString> map = new LinkedHashMap<Enum<?>, SerializableString>();
            for (final Enum<?> en : this._values) {
                map.put(en, this._textual[en.ordinal()]);
            }
            result = new EnumMap<Object, SerializableString>(map);
        }
        return result;
    }
    
    public Class<Enum<?>> getEnumClass() {
        return this._enumClass;
    }
}
