// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.joda;

import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.util.StringUtils;
import org.joda.time.format.DateTimeFormatter;
import java.util.TimeZone;
import org.springframework.format.annotation.DateTimeFormat;

public class DateTimeFormatterFactory
{
    private String pattern;
    private DateTimeFormat.ISO iso;
    private String style;
    private TimeZone timeZone;
    
    public DateTimeFormatterFactory() {
    }
    
    public DateTimeFormatterFactory(final String pattern) {
        this.pattern = pattern;
    }
    
    public void setPattern(final String pattern) {
        this.pattern = pattern;
    }
    
    public void setIso(final DateTimeFormat.ISO iso) {
        this.iso = iso;
    }
    
    public void setStyle(final String style) {
        this.style = style;
    }
    
    public void setTimeZone(final TimeZone timeZone) {
        this.timeZone = timeZone;
    }
    
    public DateTimeFormatter createDateTimeFormatter() {
        return this.createDateTimeFormatter(org.joda.time.format.DateTimeFormat.mediumDateTime());
    }
    
    public DateTimeFormatter createDateTimeFormatter(final DateTimeFormatter fallbackFormatter) {
        DateTimeFormatter dateTimeFormatter = null;
        if (StringUtils.hasLength(this.pattern)) {
            dateTimeFormatter = org.joda.time.format.DateTimeFormat.forPattern(this.pattern);
        }
        else if (this.iso != null && this.iso != DateTimeFormat.ISO.NONE) {
            switch (this.iso) {
                case DATE: {
                    dateTimeFormatter = ISODateTimeFormat.date();
                    break;
                }
                case TIME: {
                    dateTimeFormatter = ISODateTimeFormat.time();
                    break;
                }
                case DATE_TIME: {
                    dateTimeFormatter = ISODateTimeFormat.dateTime();
                    break;
                }
                case NONE: {
                    break;
                }
                default: {
                    throw new IllegalStateException("Unsupported ISO format: " + this.iso);
                }
            }
        }
        else if (StringUtils.hasLength(this.style)) {
            dateTimeFormatter = org.joda.time.format.DateTimeFormat.forStyle(this.style);
        }
        if (dateTimeFormatter != null && this.timeZone != null) {
            dateTimeFormatter = dateTimeFormatter.withZone(DateTimeZone.forTimeZone(this.timeZone));
        }
        return (dateTimeFormatter != null) ? dateTimeFormatter : fallbackFormatter;
    }
}
