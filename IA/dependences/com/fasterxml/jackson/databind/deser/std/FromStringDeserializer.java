// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.core.JsonToken;
import java.net.MalformedURLException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.util.TimeZone;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.Currency;
import com.fasterxml.jackson.databind.JavaType;
import java.net.URI;
import java.net.URL;
import java.io.File;

public abstract class FromStringDeserializer<T> extends StdScalarDeserializer<T>
{
    public static Class<?>[] types() {
        return (Class<?>[])new Class[] { File.class, URL.class, URI.class, Class.class, JavaType.class, Currency.class, Pattern.class, Locale.class, Charset.class, TimeZone.class, InetAddress.class, InetSocketAddress.class, StringBuilder.class };
    }
    
    protected FromStringDeserializer(final Class<?> vc) {
        super(vc);
    }
    
    public static Std findDeserializer(final Class<?> rawType) {
        int kind = 0;
        if (rawType == File.class) {
            kind = 1;
        }
        else if (rawType == URL.class) {
            kind = 2;
        }
        else if (rawType == URI.class) {
            kind = 3;
        }
        else if (rawType == Class.class) {
            kind = 4;
        }
        else if (rawType == JavaType.class) {
            kind = 5;
        }
        else if (rawType == Currency.class) {
            kind = 6;
        }
        else if (rawType == Pattern.class) {
            kind = 7;
        }
        else if (rawType == Locale.class) {
            kind = 8;
        }
        else if (rawType == Charset.class) {
            kind = 9;
        }
        else if (rawType == TimeZone.class) {
            kind = 10;
        }
        else if (rawType == InetAddress.class) {
            kind = 11;
        }
        else if (rawType == InetSocketAddress.class) {
            kind = 12;
        }
        else {
            if (rawType != StringBuilder.class) {
                return null;
            }
            kind = 13;
        }
        return new Std(rawType, kind);
    }
    
    @Override
    public T deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        String text = p.getValueAsString();
        if (text != null) {
            if (text.length() == 0 || (text = text.trim()).length() == 0) {
                return this._deserializeFromEmptyString();
            }
            Exception cause = null;
            try {
                return this._deserialize(text, ctxt);
            }
            catch (IllegalArgumentException | MalformedURLException ex2) {
                final Exception ex;
                final Exception e = cause = ex;
                String msg = "not a valid textual representation";
                final String m2 = cause.getMessage();
                if (m2 != null) {
                    msg = msg + ", problem: " + m2;
                }
                final JsonMappingException e2 = ctxt.weirdStringException(text, this._valueClass, msg);
                e2.initCause(cause);
                throw e2;
            }
        }
        final JsonToken t = p.getCurrentToken();
        if (t == JsonToken.START_ARRAY) {
            return this._deserializeFromArray(p, ctxt);
        }
        if (t != JsonToken.VALUE_EMBEDDED_OBJECT) {
            return (T)ctxt.handleUnexpectedToken(this._valueClass, p);
        }
        final Object ob = p.getEmbeddedObject();
        if (ob == null) {
            return null;
        }
        if (this._valueClass.isAssignableFrom(ob.getClass())) {
            return (T)ob;
        }
        return this._deserializeEmbedded(ob, ctxt);
    }
    
    protected abstract T _deserialize(final String p0, final DeserializationContext p1) throws IOException;
    
    protected T _deserializeEmbedded(final Object ob, final DeserializationContext ctxt) throws IOException {
        ctxt.reportInputMismatch(this, "Don't know how to convert embedded Object of type %s into %s", ob.getClass().getName(), this._valueClass.getName());
        return null;
    }
    
    protected T _deserializeFromEmptyString() throws IOException {
        return null;
    }
    
    public static class Std extends FromStringDeserializer<Object>
    {
        private static final long serialVersionUID = 1L;
        public static final int STD_FILE = 1;
        public static final int STD_URL = 2;
        public static final int STD_URI = 3;
        public static final int STD_CLASS = 4;
        public static final int STD_JAVA_TYPE = 5;
        public static final int STD_CURRENCY = 6;
        public static final int STD_PATTERN = 7;
        public static final int STD_LOCALE = 8;
        public static final int STD_CHARSET = 9;
        public static final int STD_TIME_ZONE = 10;
        public static final int STD_INET_ADDRESS = 11;
        public static final int STD_INET_SOCKET_ADDRESS = 12;
        public static final int STD_STRING_BUILDER = 13;
        protected final int _kind;
        
        protected Std(final Class<?> valueType, final int kind) {
            super(valueType);
            this._kind = kind;
        }
        
        @Override
        protected Object _deserialize(String value, final DeserializationContext ctxt) throws IOException {
            switch (this._kind) {
                case 1: {
                    return new File(value);
                }
                case 2: {
                    return new URL(value);
                }
                case 3: {
                    return URI.create(value);
                }
                case 4: {
                    try {
                        return ctxt.findClass(value);
                    }
                    catch (Exception e) {
                        return ctxt.handleInstantiationProblem(this._valueClass, value, ClassUtil.getRootCause(e));
                    }
                }
                case 5: {
                    return ctxt.getTypeFactory().constructFromCanonical(value);
                }
                case 6: {
                    return Currency.getInstance(value);
                }
                case 7: {
                    return Pattern.compile(value);
                }
                case 8: {
                    int ix = this._firstHyphenOrUnderscore(value);
                    if (ix < 0) {
                        return new Locale(value);
                    }
                    final String first = value.substring(0, ix);
                    value = value.substring(ix + 1);
                    ix = this._firstHyphenOrUnderscore(value);
                    if (ix < 0) {
                        return new Locale(first, value);
                    }
                    final String second = value.substring(0, ix);
                    return new Locale(first, second, value.substring(ix + 1));
                }
                case 9: {
                    return Charset.forName(value);
                }
                case 10: {
                    return TimeZone.getTimeZone(value);
                }
                case 11: {
                    return InetAddress.getByName(value);
                }
                case 12: {
                    if (value.startsWith("[")) {
                        final int i = value.lastIndexOf(93);
                        if (i == -1) {
                            throw new InvalidFormatException(ctxt.getParser(), "Bracketed IPv6 address must contain closing bracket", value, InetSocketAddress.class);
                        }
                        final int j = value.indexOf(58, i);
                        final int port = (j > -1) ? Integer.parseInt(value.substring(j + 1)) : 0;
                        return new InetSocketAddress(value.substring(0, i + 1), port);
                    }
                    else {
                        final int ix = value.indexOf(58);
                        if (ix >= 0 && value.indexOf(58, ix + 1) < 0) {
                            final int port2 = Integer.parseInt(value.substring(ix + 1));
                            return new InetSocketAddress(value.substring(0, ix), port2);
                        }
                        return new InetSocketAddress(value, 0);
                    }
                    break;
                }
                case 13: {
                    return new StringBuilder(value);
                }
                default: {
                    VersionUtil.throwInternal();
                    return null;
                }
            }
        }
        
        @Override
        protected Object _deserializeFromEmptyString() throws IOException {
            if (this._kind == 3) {
                return URI.create("");
            }
            if (this._kind == 8) {
                return Locale.ROOT;
            }
            if (this._kind == 13) {
                return new StringBuilder();
            }
            return super._deserializeFromEmptyString();
        }
        
        protected int _firstHyphenOrUnderscore(final String str) {
            for (int i = 0, end = str.length(); i < end; ++i) {
                final char c = str.charAt(i);
                if (c == '_' || c == '-') {
                    return i;
                }
            }
            return -1;
        }
    }
}
