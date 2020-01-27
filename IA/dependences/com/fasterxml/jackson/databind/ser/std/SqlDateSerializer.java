// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import java.io.IOException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import java.text.DateFormat;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import java.sql.Date;

@JacksonStdImpl
public class SqlDateSerializer extends DateTimeSerializerBase<Date>
{
    public SqlDateSerializer() {
        this(null, null);
    }
    
    protected SqlDateSerializer(final Boolean useTimestamp, final DateFormat customFormat) {
        super(Date.class, useTimestamp, customFormat);
    }
    
    @Override
    public SqlDateSerializer withFormat(final Boolean timestamp, final DateFormat customFormat) {
        return new SqlDateSerializer(timestamp, customFormat);
    }
    
    @Override
    protected long _timestamp(final Date value) {
        return (value == null) ? 0L : value.getTime();
    }
    
    @Override
    public void serialize(final Date value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
        if (this._asTimestamp(provider)) {
            g.writeNumber(this._timestamp(value));
            return;
        }
        if (this._customFormat == null) {
            g.writeString(value.toString());
            return;
        }
        this._serializeAsString(value, g, provider);
    }
}
