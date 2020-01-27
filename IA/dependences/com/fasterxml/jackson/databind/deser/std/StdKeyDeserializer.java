// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.util.EnumResolver;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.NumberInput;
import java.net.MalformedURLException;
import java.io.IOException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.util.Currency;
import java.util.Locale;
import java.net.URL;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import java.io.Serializable;
import com.fasterxml.jackson.databind.KeyDeserializer;

@JacksonStdImpl
public class StdKeyDeserializer extends KeyDeserializer implements Serializable
{
    private static final long serialVersionUID = 1L;
    public static final int TYPE_BOOLEAN = 1;
    public static final int TYPE_BYTE = 2;
    public static final int TYPE_SHORT = 3;
    public static final int TYPE_CHAR = 4;
    public static final int TYPE_INT = 5;
    public static final int TYPE_LONG = 6;
    public static final int TYPE_FLOAT = 7;
    public static final int TYPE_DOUBLE = 8;
    public static final int TYPE_LOCALE = 9;
    public static final int TYPE_DATE = 10;
    public static final int TYPE_CALENDAR = 11;
    public static final int TYPE_UUID = 12;
    public static final int TYPE_URI = 13;
    public static final int TYPE_URL = 14;
    public static final int TYPE_CLASS = 15;
    public static final int TYPE_CURRENCY = 16;
    public static final int TYPE_BYTE_ARRAY = 17;
    protected final int _kind;
    protected final Class<?> _keyClass;
    protected final FromStringDeserializer<?> _deser;
    
    protected StdKeyDeserializer(final int kind, final Class<?> cls) {
        this(kind, cls, null);
    }
    
    protected StdKeyDeserializer(final int kind, final Class<?> cls, final FromStringDeserializer<?> deser) {
        this._kind = kind;
        this._keyClass = cls;
        this._deser = deser;
    }
    
    public static StdKeyDeserializer forType(final Class<?> raw) {
        if (raw == String.class || raw == Object.class || raw == CharSequence.class) {
            return StringKD.forType(raw);
        }
        int kind;
        if (raw == UUID.class) {
            kind = 12;
        }
        else if (raw == Integer.class) {
            kind = 5;
        }
        else if (raw == Long.class) {
            kind = 6;
        }
        else if (raw == Date.class) {
            kind = 10;
        }
        else if (raw == Calendar.class) {
            kind = 11;
        }
        else if (raw == Boolean.class) {
            kind = 1;
        }
        else if (raw == Byte.class) {
            kind = 2;
        }
        else if (raw == Character.class) {
            kind = 4;
        }
        else if (raw == Short.class) {
            kind = 3;
        }
        else if (raw == Float.class) {
            kind = 7;
        }
        else if (raw == Double.class) {
            kind = 8;
        }
        else if (raw == URI.class) {
            kind = 13;
        }
        else if (raw == URL.class) {
            kind = 14;
        }
        else if (raw == Class.class) {
            kind = 15;
        }
        else {
            if (raw == Locale.class) {
                final FromStringDeserializer<?> deser = FromStringDeserializer.findDeserializer(Locale.class);
                return new StdKeyDeserializer(9, raw, deser);
            }
            if (raw == Currency.class) {
                final FromStringDeserializer<?> deser = FromStringDeserializer.findDeserializer(Currency.class);
                return new StdKeyDeserializer(16, raw, deser);
            }
            if (raw != byte[].class) {
                return null;
            }
            kind = 17;
        }
        return new StdKeyDeserializer(kind, raw);
    }
    
    @Override
    public Object deserializeKey(final String key, final DeserializationContext ctxt) throws IOException {
        if (key == null) {
            return null;
        }
        try {
            final Object result = this._parse(key, ctxt);
            if (result != null) {
                return result;
            }
        }
        catch (Exception re) {
            return ctxt.handleWeirdKey(this._keyClass, key, "not a valid representation, problem: (%s) %s", re.getClass().getName(), ClassUtil.exceptionMessage(re));
        }
        if (this._keyClass.isEnum() && ctxt.getConfig().isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
            return null;
        }
        return ctxt.handleWeirdKey(this._keyClass, key, "not a valid representation", new Object[0]);
    }
    
    public Class<?> getKeyClass() {
        return this._keyClass;
    }
    
    protected Object _parse(final String key, final DeserializationContext ctxt) throws Exception {
        switch (this._kind) {
            case 1: {
                if ("true".equals(key)) {
                    return Boolean.TRUE;
                }
                if ("false".equals(key)) {
                    return Boolean.FALSE;
                }
                return ctxt.handleWeirdKey(this._keyClass, key, "value not 'true' or 'false'", new Object[0]);
            }
            case 2: {
                final int value = this._parseInt(key);
                if (value < -128 || value > 255) {
                    return ctxt.handleWeirdKey(this._keyClass, key, "overflow, value cannot be represented as 8-bit value", new Object[0]);
                }
                return (byte)value;
            }
            case 3: {
                final int value = this._parseInt(key);
                if (value < -32768 || value > 32767) {
                    return ctxt.handleWeirdKey(this._keyClass, key, "overflow, value cannot be represented as 16-bit value", new Object[0]);
                }
                return (short)value;
            }
            case 4: {
                if (key.length() == 1) {
                    return key.charAt(0);
                }
                return ctxt.handleWeirdKey(this._keyClass, key, "can only convert 1-character Strings", new Object[0]);
            }
            case 5: {
                return this._parseInt(key);
            }
            case 6: {
                return this._parseLong(key);
            }
            case 7: {
                return (float)this._parseDouble(key);
            }
            case 8: {
                return this._parseDouble(key);
            }
            case 9: {
                try {
                    return this._deser._deserialize(key, ctxt);
                }
                catch (IllegalArgumentException e) {
                    return this._weirdKey(ctxt, key, e);
                }
            }
            case 16: {
                try {
                    return this._deser._deserialize(key, ctxt);
                }
                catch (IllegalArgumentException e) {
                    return this._weirdKey(ctxt, key, e);
                }
            }
            case 10: {
                return ctxt.parseDate(key);
            }
            case 11: {
                return ctxt.constructCalendar(ctxt.parseDate(key));
            }
            case 12: {
                try {
                    return UUID.fromString(key);
                }
                catch (Exception e2) {
                    return this._weirdKey(ctxt, key, e2);
                }
            }
            case 13: {
                try {
                    return URI.create(key);
                }
                catch (Exception e2) {
                    return this._weirdKey(ctxt, key, e2);
                }
            }
            case 14: {
                try {
                    return new URL(key);
                }
                catch (MalformedURLException e3) {
                    return this._weirdKey(ctxt, key, e3);
                }
            }
            case 15: {
                try {
                    return ctxt.findClass(key);
                }
                catch (Exception e2) {
                    return ctxt.handleWeirdKey(this._keyClass, key, "unable to parse key as Class", new Object[0]);
                }
            }
            case 17: {
                try {
                    return ctxt.getConfig().getBase64Variant().decode(key);
                }
                catch (IllegalArgumentException e) {
                    return this._weirdKey(ctxt, key, e);
                }
                break;
            }
        }
        throw new IllegalStateException("Internal error: unknown key type " + this._keyClass);
    }
    
    protected int _parseInt(final String key) throws IllegalArgumentException {
        return Integer.parseInt(key);
    }
    
    protected long _parseLong(final String key) throws IllegalArgumentException {
        return Long.parseLong(key);
    }
    
    protected double _parseDouble(final String key) throws IllegalArgumentException {
        return NumberInput.parseDouble(key);
    }
    
    protected Object _weirdKey(final DeserializationContext ctxt, final String key, final Exception e) throws IOException {
        return ctxt.handleWeirdKey(this._keyClass, key, "problem: %s", ClassUtil.exceptionMessage(e));
    }
    
    @JacksonStdImpl
    static final class StringKD extends StdKeyDeserializer
    {
        private static final long serialVersionUID = 1L;
        private static final StringKD sString;
        private static final StringKD sObject;
        
        private StringKD(final Class<?> nominalType) {
            super(-1, nominalType);
        }
        
        public static StringKD forType(final Class<?> nominalType) {
            if (nominalType == String.class) {
                return StringKD.sString;
            }
            if (nominalType == Object.class) {
                return StringKD.sObject;
            }
            return new StringKD(nominalType);
        }
        
        @Override
        public Object deserializeKey(final String key, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return key;
        }
        
        static {
            sString = new StringKD(String.class);
            sObject = new StringKD(Object.class);
        }
    }
    
    static final class DelegatingKD extends KeyDeserializer implements Serializable
    {
        private static final long serialVersionUID = 1L;
        protected final Class<?> _keyClass;
        protected final JsonDeserializer<?> _delegate;
        
        protected DelegatingKD(final Class<?> cls, final JsonDeserializer<?> deser) {
            this._keyClass = cls;
            this._delegate = deser;
        }
        
        @Override
        public final Object deserializeKey(final String key, final DeserializationContext ctxt) throws IOException {
            if (key == null) {
                return null;
            }
            final TokenBuffer tb = new TokenBuffer(ctxt.getParser(), ctxt);
            tb.writeString(key);
            try {
                final JsonParser p = tb.asParser();
                p.nextToken();
                final Object result = this._delegate.deserialize(p, ctxt);
                if (result != null) {
                    return result;
                }
                return ctxt.handleWeirdKey(this._keyClass, key, "not a valid representation", new Object[0]);
            }
            catch (Exception re) {
                return ctxt.handleWeirdKey(this._keyClass, key, "not a valid representation: %s", re.getMessage());
            }
        }
        
        public Class<?> getKeyClass() {
            return this._keyClass;
        }
    }
    
    @JacksonStdImpl
    static final class EnumKD extends StdKeyDeserializer
    {
        private static final long serialVersionUID = 1L;
        protected final EnumResolver _byNameResolver;
        protected final AnnotatedMethod _factory;
        protected EnumResolver _byToStringResolver;
        protected final Enum<?> _enumDefaultValue;
        
        protected EnumKD(final EnumResolver er, final AnnotatedMethod factory) {
            super(-1, er.getEnumClass());
            this._byNameResolver = er;
            this._factory = factory;
            this._enumDefaultValue = er.getDefaultValue();
        }
        
        public Object _parse(final String key, final DeserializationContext ctxt) throws IOException {
            if (this._factory != null) {
                try {
                    return this._factory.call1(key);
                }
                catch (Exception e) {
                    ClassUtil.unwrapAndThrowAsIAE(e);
                }
            }
            final EnumResolver res = ctxt.isEnabled(DeserializationFeature.READ_ENUMS_USING_TO_STRING) ? this._getToStringResolver(ctxt) : this._byNameResolver;
            Enum<?> e2 = res.findEnum(key);
            if (e2 == null) {
                if (this._enumDefaultValue != null && ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)) {
                    e2 = this._enumDefaultValue;
                }
                else if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
                    return ctxt.handleWeirdKey(this._keyClass, key, "not one of values excepted for Enum class: %s", res.getEnumIds());
                }
            }
            return e2;
        }
        
        private EnumResolver _getToStringResolver(final DeserializationContext ctxt) {
            EnumResolver res = this._byToStringResolver;
            if (res == null) {
                synchronized (this) {
                    res = EnumResolver.constructUnsafeUsingToString(this._byNameResolver.getEnumClass(), ctxt.getAnnotationIntrospector());
                    this._byToStringResolver = res;
                }
            }
            return res;
        }
    }
    
    static final class StringCtorKeyDeserializer extends StdKeyDeserializer
    {
        private static final long serialVersionUID = 1L;
        protected final Constructor<?> _ctor;
        
        public StringCtorKeyDeserializer(final Constructor<?> ctor) {
            super(-1, ctor.getDeclaringClass());
            this._ctor = ctor;
        }
        
        public Object _parse(final String key, final DeserializationContext ctxt) throws Exception {
            return this._ctor.newInstance(key);
        }
    }
    
    static final class StringFactoryKeyDeserializer extends StdKeyDeserializer
    {
        private static final long serialVersionUID = 1L;
        final Method _factoryMethod;
        
        public StringFactoryKeyDeserializer(final Method fm) {
            super(-1, fm.getDeclaringClass());
            this._factoryMethod = fm;
        }
        
        public Object _parse(final String key, final DeserializationContext ctxt) throws Exception {
            return this._factoryMethod.invoke(null, key);
        }
    }
}
