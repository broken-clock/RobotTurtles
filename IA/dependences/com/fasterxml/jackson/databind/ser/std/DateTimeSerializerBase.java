// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import java.util.Date;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.util.TimeZone;
import java.util.Locale;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.text.SimpleDateFormat;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.util.concurrent.atomic.AtomicReference;
import java.text.DateFormat;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

public abstract class DateTimeSerializerBase<T> extends StdScalarSerializer<T> implements ContextualSerializer
{
    protected final Boolean _useTimestamp;
    protected final DateFormat _customFormat;
    protected final AtomicReference<DateFormat> _reusedCustomFormat;
    
    protected DateTimeSerializerBase(final Class<T> type, final Boolean useTimestamp, final DateFormat customFormat) {
        super(type);
        this._useTimestamp = useTimestamp;
        this._customFormat = customFormat;
        this._reusedCustomFormat = ((customFormat == null) ? null : new AtomicReference<DateFormat>());
    }
    
    public abstract DateTimeSerializerBase<T> withFormat(final Boolean p0, final DateFormat p1);
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider serializers, final BeanProperty property) throws JsonMappingException {
        final JsonFormat.Value format = this.findFormatOverrides(serializers, property, this.handledType());
        if (format == null) {
            return this;
        }
        final JsonFormat.Shape shape = format.getShape();
        if (shape.isNumeric()) {
            return this.withFormat(Boolean.TRUE, null);
        }
        if (format.hasPattern()) {
            final Locale loc = format.hasLocale() ? format.getLocale() : serializers.getLocale();
            final SimpleDateFormat df = new SimpleDateFormat(format.getPattern(), loc);
            final TimeZone tz = format.hasTimeZone() ? format.getTimeZone() : serializers.getTimeZone();
            df.setTimeZone(tz);
            return this.withFormat(Boolean.FALSE, df);
        }
        final boolean hasLocale = format.hasLocale();
        final boolean hasTZ = format.hasTimeZone();
        final boolean asString = shape == JsonFormat.Shape.STRING;
        if (!hasLocale && !hasTZ && !asString) {
            return this;
        }
        final DateFormat df2 = serializers.getConfig().getDateFormat();
        if (df2 instanceof StdDateFormat) {
            StdDateFormat std = (StdDateFormat)df2;
            if (format.hasLocale()) {
                std = std.withLocale(format.getLocale());
            }
            if (format.hasTimeZone()) {
                std = std.withTimeZone(format.getTimeZone());
            }
            return this.withFormat(Boolean.FALSE, std);
        }
        if (!(df2 instanceof SimpleDateFormat)) {
            serializers.reportBadDefinition(this.handledType(), String.format("Configured `DateFormat` (%s) not a `SimpleDateFormat`; cannot configure `Locale` or `TimeZone`", df2.getClass().getName()));
        }
        SimpleDateFormat df3 = (SimpleDateFormat)df2;
        if (hasLocale) {
            df3 = new SimpleDateFormat(df3.toPattern(), format.getLocale());
        }
        else {
            df3 = (SimpleDateFormat)df3.clone();
        }
        final TimeZone newTz = format.getTimeZone();
        final boolean changeTZ = newTz != null && !newTz.equals(df3.getTimeZone());
        if (changeTZ) {
            df3.setTimeZone(newTz);
        }
        return this.withFormat(Boolean.FALSE, df3);
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider serializers, final T value) {
        return false;
    }
    
    protected abstract long _timestamp(final T p0);
    
    @Override
    public JsonNode getSchema(final SerializerProvider serializers, final Type typeHint) {
        return this.createSchemaNode(this._asTimestamp(serializers) ? "number" : "string", true);
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        this._acceptJsonFormatVisitor(visitor, typeHint, this._asTimestamp(visitor.getProvider()));
    }
    
    @Override
    public abstract void serialize(final T p0, final JsonGenerator p1, final SerializerProvider p2) throws IOException;
    
    protected boolean _asTimestamp(final SerializerProvider serializers) {
        if (this._useTimestamp != null) {
            return this._useTimestamp;
        }
        if (this._customFormat != null) {
            return false;
        }
        if (serializers != null) {
            return serializers.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }
        throw new IllegalArgumentException("Null SerializerProvider passed for " + this.handledType().getName());
    }
    
    protected void _acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint, final boolean asNumber) throws JsonMappingException {
        if (asNumber) {
            this.visitIntFormat(visitor, typeHint, JsonParser.NumberType.LONG, JsonValueFormat.UTC_MILLISEC);
        }
        else {
            this.visitStringFormat(visitor, typeHint, JsonValueFormat.DATE_TIME);
        }
    }
    
    protected void _serializeAsString(final Date value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
        if (this._customFormat == null) {
            provider.defaultSerializeDateValue(value, g);
            return;
        }
        DateFormat f = this._reusedCustomFormat.getAndSet(null);
        if (f == null) {
            f = (DateFormat)this._customFormat.clone();
        }
        g.writeString(f.format(value));
        this._reusedCustomFormat.compareAndSet(null, f);
    }
}
