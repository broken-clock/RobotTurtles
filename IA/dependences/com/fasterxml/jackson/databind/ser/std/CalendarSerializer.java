// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import java.io.IOException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import java.text.DateFormat;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import java.util.Calendar;

@JacksonStdImpl
public class CalendarSerializer extends DateTimeSerializerBase<Calendar>
{
    public static final CalendarSerializer instance;
    
    public CalendarSerializer() {
        this(null, null);
    }
    
    public CalendarSerializer(final Boolean useTimestamp, final DateFormat customFormat) {
        super(Calendar.class, useTimestamp, customFormat);
    }
    
    @Override
    public CalendarSerializer withFormat(final Boolean timestamp, final DateFormat customFormat) {
        return new CalendarSerializer(timestamp, customFormat);
    }
    
    @Override
    protected long _timestamp(final Calendar value) {
        return (value == null) ? 0L : value.getTimeInMillis();
    }
    
    @Override
    public void serialize(final Calendar value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
        if (this._asTimestamp(provider)) {
            g.writeNumber(this._timestamp(value));
            return;
        }
        this._serializeAsString(value.getTime(), g, provider);
    }
    
    static {
        instance = new CalendarSerializer();
    }
}
