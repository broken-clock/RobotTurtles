// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Constructor;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import java.io.IOException;
import java.text.ParseException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.util.Locale;
import java.util.TimeZone;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import java.text.SimpleDateFormat;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.text.DateFormat;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.util.GregorianCalendar;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Calendar;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.util.HashSet;

public class DateDeserializers
{
    private static final HashSet<String> _classNames;
    
    public static JsonDeserializer<?> find(final Class<?> rawType, final String clsName) {
        if (DateDeserializers._classNames.contains(clsName)) {
            if (rawType == Calendar.class) {
                return new CalendarDeserializer();
            }
            if (rawType == Date.class) {
                return DateDeserializer.instance;
            }
            if (rawType == java.sql.Date.class) {
                return new SqlDateDeserializer();
            }
            if (rawType == Timestamp.class) {
                return new TimestampDeserializer();
            }
            if (rawType == GregorianCalendar.class) {
                return new CalendarDeserializer(GregorianCalendar.class);
            }
        }
        return null;
    }
    
    static {
        _classNames = new HashSet<String>();
        final Class[] array;
        final Class<?>[] numberTypes = (Class<?>[])(array = new Class[] { Calendar.class, GregorianCalendar.class, java.sql.Date.class, Date.class, Timestamp.class });
        for (final Class<?> cls : array) {
            DateDeserializers._classNames.add(cls.getName());
        }
    }
    
    protected abstract static class DateBasedDeserializer<T> extends StdScalarDeserializer<T> implements ContextualDeserializer
    {
        protected final DateFormat _customFormat;
        protected final String _formatString;
        
        protected DateBasedDeserializer(final Class<?> clz) {
            super(clz);
            this._customFormat = null;
            this._formatString = null;
        }
        
        protected DateBasedDeserializer(final DateBasedDeserializer<T> base, final DateFormat format, final String formatStr) {
            super(base._valueClass);
            this._customFormat = format;
            this._formatString = formatStr;
        }
        
        protected abstract DateBasedDeserializer<T> withDateFormat(final DateFormat p0, final String p1);
        
        @Override
        public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
            final JsonFormat.Value format = this.findFormatOverrides(ctxt, property, this.handledType());
            if (format != null) {
                TimeZone tz = format.getTimeZone();
                final Boolean lenient = format.getLenient();
                if (format.hasPattern()) {
                    final String pattern = format.getPattern();
                    final Locale loc = format.hasLocale() ? format.getLocale() : ctxt.getLocale();
                    final SimpleDateFormat df = new SimpleDateFormat(pattern, loc);
                    if (tz == null) {
                        tz = ctxt.getTimeZone();
                    }
                    df.setTimeZone(tz);
                    if (lenient != null) {
                        df.setLenient(lenient);
                    }
                    return this.withDateFormat(df, pattern);
                }
                if (tz != null) {
                    DateFormat df2 = ctxt.getConfig().getDateFormat();
                    if (df2.getClass() == StdDateFormat.class) {
                        final Locale loc = format.hasLocale() ? format.getLocale() : ctxt.getLocale();
                        StdDateFormat std = (StdDateFormat)df2;
                        std = std.withTimeZone(tz);
                        std = std.withLocale(loc);
                        if (lenient != null) {
                            std = std.withLenient(lenient);
                        }
                        df2 = std;
                    }
                    else {
                        df2 = (DateFormat)df2.clone();
                        df2.setTimeZone(tz);
                        if (lenient != null) {
                            df2.setLenient(lenient);
                        }
                    }
                    return this.withDateFormat(df2, this._formatString);
                }
                if (lenient != null) {
                    DateFormat df2 = ctxt.getConfig().getDateFormat();
                    String pattern2 = this._formatString;
                    if (df2.getClass() == StdDateFormat.class) {
                        StdDateFormat std = (StdDateFormat)df2;
                        std = (StdDateFormat)(df2 = std.withLenient(lenient));
                        pattern2 = std.toPattern();
                    }
                    else {
                        df2 = (DateFormat)df2.clone();
                        df2.setLenient(lenient);
                        if (df2 instanceof SimpleDateFormat) {
                            ((SimpleDateFormat)df2).toPattern();
                        }
                    }
                    if (pattern2 == null) {
                        pattern2 = "[unknown]";
                    }
                    return this.withDateFormat(df2, pattern2);
                }
            }
            return this;
        }
        
        @Override
        protected Date _parseDate(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            if (this._customFormat != null && p.hasToken(JsonToken.VALUE_STRING)) {
                final String str = p.getText().trim();
                if (str.length() == 0) {
                    return (Date)this.getEmptyValue(ctxt);
                }
                synchronized (this._customFormat) {
                    try {
                        return this._customFormat.parse(str);
                    }
                    catch (ParseException e) {
                        return (Date)ctxt.handleWeirdStringValue(this.handledType(), str, "expected format \"%s\"", this._formatString);
                    }
                }
            }
            return super._parseDate(p, ctxt);
        }
    }
    
    @JacksonStdImpl
    public static class CalendarDeserializer extends DateBasedDeserializer<Calendar>
    {
        protected final Constructor<Calendar> _defaultCtor;
        
        public CalendarDeserializer() {
            super(Calendar.class);
            this._defaultCtor = null;
        }
        
        public CalendarDeserializer(final Class<? extends Calendar> cc) {
            super(cc);
            this._defaultCtor = ClassUtil.findConstructor(cc, false);
        }
        
        public CalendarDeserializer(final CalendarDeserializer src, final DateFormat df, final String formatString) {
            super(src, df, formatString);
            this._defaultCtor = src._defaultCtor;
        }
        
        @Override
        protected CalendarDeserializer withDateFormat(final DateFormat df, final String formatString) {
            return new CalendarDeserializer(this, df, formatString);
        }
        
        @Override
        public Calendar deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            final Date d = this._parseDate(p, ctxt);
            if (d == null) {
                return null;
            }
            if (this._defaultCtor == null) {
                return ctxt.constructCalendar(d);
            }
            try {
                final Calendar c = this._defaultCtor.newInstance(new Object[0]);
                c.setTimeInMillis(d.getTime());
                final TimeZone tz = ctxt.getTimeZone();
                if (tz != null) {
                    c.setTimeZone(tz);
                }
                return c;
            }
            catch (Exception e) {
                return (Calendar)ctxt.handleInstantiationProblem(this.handledType(), d, e);
            }
        }
    }
    
    @JacksonStdImpl
    public static class DateDeserializer extends DateBasedDeserializer<Date>
    {
        public static final DateDeserializer instance;
        
        public DateDeserializer() {
            super(Date.class);
        }
        
        public DateDeserializer(final DateDeserializer base, final DateFormat df, final String formatString) {
            super(base, df, formatString);
        }
        
        @Override
        protected DateDeserializer withDateFormat(final DateFormat df, final String formatString) {
            return new DateDeserializer(this, df, formatString);
        }
        
        @Override
        public Date deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            return this._parseDate(p, ctxt);
        }
        
        static {
            instance = new DateDeserializer();
        }
    }
    
    public static class SqlDateDeserializer extends DateBasedDeserializer<java.sql.Date>
    {
        public SqlDateDeserializer() {
            super(java.sql.Date.class);
        }
        
        public SqlDateDeserializer(final SqlDateDeserializer src, final DateFormat df, final String formatString) {
            super(src, df, formatString);
        }
        
        @Override
        protected SqlDateDeserializer withDateFormat(final DateFormat df, final String formatString) {
            return new SqlDateDeserializer(this, df, formatString);
        }
        
        @Override
        public java.sql.Date deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            final Date d = this._parseDate(p, ctxt);
            return (d == null) ? null : new java.sql.Date(d.getTime());
        }
    }
    
    public static class TimestampDeserializer extends DateBasedDeserializer<Timestamp>
    {
        public TimestampDeserializer() {
            super(Timestamp.class);
        }
        
        public TimestampDeserializer(final TimestampDeserializer src, final DateFormat df, final String formatString) {
            super(src, df, formatString);
        }
        
        @Override
        protected TimestampDeserializer withDateFormat(final DateFormat df, final String formatString) {
            return new TimestampDeserializer(this, df, formatString);
        }
        
        @Override
        public Timestamp deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            final Date d = this._parseDate(p, ctxt);
            return (d == null) ? null : new Timestamp(d.getTime());
        }
    }
}
