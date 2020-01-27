// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime.standard;

import org.springframework.util.StringUtils;
import java.time.format.DateTimeFormatter;
import org.springframework.util.Assert;
import java.util.TimeZone;
import java.time.format.FormatStyle;
import org.springframework.format.annotation.DateTimeFormat;

public class DateTimeFormatterFactory
{
    private String pattern;
    private DateTimeFormat.ISO iso;
    private FormatStyle dateStyle;
    private FormatStyle timeStyle;
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
    
    public void setDateStyle(final FormatStyle dateStyle) {
        this.dateStyle = dateStyle;
    }
    
    public void setTimeStyle(final FormatStyle timeStyle) {
        this.timeStyle = timeStyle;
    }
    
    public void setDateTimeStyle(final FormatStyle dateTimeStyle) {
        this.dateStyle = dateTimeStyle;
        this.timeStyle = dateTimeStyle;
    }
    
    public void setStylePattern(final String style) {
        Assert.isTrue(style != null && style.length() == 2);
        this.dateStyle = this.convertStyleCharacter(style.charAt(0));
        this.timeStyle = this.convertStyleCharacter(style.charAt(1));
    }
    
    private FormatStyle convertStyleCharacter(final char c) {
        switch (c) {
            case 'S': {
                return FormatStyle.SHORT;
            }
            case 'M': {
                return FormatStyle.MEDIUM;
            }
            case 'L': {
                return FormatStyle.LONG;
            }
            case 'F': {
                return FormatStyle.FULL;
            }
            case '-': {
                return null;
            }
            default: {
                throw new IllegalArgumentException("Invalid style character '" + c + "'");
            }
        }
    }
    
    public void setTimeZone(final TimeZone timeZone) {
        this.timeZone = timeZone;
    }
    
    public DateTimeFormatter createDateTimeFormatter() {
        return this.createDateTimeFormatter(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    }
    
    public DateTimeFormatter createDateTimeFormatter(final DateTimeFormatter fallbackFormatter) {
        DateTimeFormatter dateTimeFormatter = null;
        if (StringUtils.hasLength(this.pattern)) {
            dateTimeFormatter = DateTimeFormatter.ofPattern(this.pattern);
        }
        else if (this.iso != null && this.iso != DateTimeFormat.ISO.NONE) {
            switch (this.iso) {
                case DATE: {
                    dateTimeFormatter = DateTimeFormatter.ISO_DATE;
                    break;
                }
                case TIME: {
                    dateTimeFormatter = DateTimeFormatter.ISO_TIME;
                    break;
                }
                case DATE_TIME: {
                    dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
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
        else if (this.dateStyle != null && this.timeStyle != null) {
            dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(this.dateStyle, this.timeStyle);
        }
        else if (this.dateStyle != null) {
            dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(this.dateStyle);
        }
        else if (this.timeStyle != null) {
            dateTimeFormatter = DateTimeFormatter.ofLocalizedTime(this.timeStyle);
        }
        if (dateTimeFormatter != null && this.timeZone != null) {
            dateTimeFormatter = dateTimeFormatter.withZone(this.timeZone.toZoneId());
        }
        return (dateTimeFormatter != null) ? dateTimeFormatter : fallbackFormatter;
    }
}
