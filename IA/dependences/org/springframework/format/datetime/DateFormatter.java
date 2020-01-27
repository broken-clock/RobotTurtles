// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.datetime;

import java.util.Collections;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import org.springframework.util.StringUtils;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.TimeZone;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Map;
import java.util.Date;
import org.springframework.format.Formatter;

public class DateFormatter implements Formatter<Date>
{
    private static final Map<DateTimeFormat.ISO, String> ISO_PATTERNS;
    private String pattern;
    private int style;
    private String stylePattern;
    private DateTimeFormat.ISO iso;
    private TimeZone timeZone;
    private boolean lenient;
    
    public DateFormatter() {
        this.style = 2;
        this.lenient = false;
    }
    
    public DateFormatter(final String pattern) {
        this.style = 2;
        this.lenient = false;
        this.pattern = pattern;
    }
    
    public void setPattern(final String pattern) {
        this.pattern = pattern;
    }
    
    public void setIso(final DateTimeFormat.ISO iso) {
        this.iso = iso;
    }
    
    public void setStyle(final int style) {
        this.style = style;
    }
    
    public void setStylePattern(final String stylePattern) {
        this.stylePattern = stylePattern;
    }
    
    public void setTimeZone(final TimeZone timeZone) {
        this.timeZone = timeZone;
    }
    
    public void setLenient(final boolean lenient) {
        this.lenient = lenient;
    }
    
    @Override
    public String print(final Date date, final Locale locale) {
        return this.getDateFormat(locale).format(date);
    }
    
    @Override
    public Date parse(final String text, final Locale locale) throws ParseException {
        return this.getDateFormat(locale).parse(text);
    }
    
    protected DateFormat getDateFormat(final Locale locale) {
        final DateFormat dateFormat = this.createDateFormat(locale);
        if (this.timeZone != null) {
            dateFormat.setTimeZone(this.timeZone);
        }
        dateFormat.setLenient(this.lenient);
        return dateFormat;
    }
    
    private DateFormat createDateFormat(final Locale locale) {
        if (StringUtils.hasLength(this.pattern)) {
            return new SimpleDateFormat(this.pattern, locale);
        }
        if (this.iso != null && this.iso != DateTimeFormat.ISO.NONE) {
            final String pattern = DateFormatter.ISO_PATTERNS.get(this.iso);
            if (pattern == null) {
                throw new IllegalStateException("Unsupported ISO format " + this.iso);
            }
            final SimpleDateFormat format = new SimpleDateFormat(pattern);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return format;
        }
        else {
            if (!StringUtils.hasLength(this.stylePattern)) {
                return DateFormat.getDateInstance(this.style, locale);
            }
            final int dateStyle = this.getStylePatternForChar(0);
            final int timeStyle = this.getStylePatternForChar(1);
            if (dateStyle != -1 && timeStyle != -1) {
                return DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
            }
            if (dateStyle != -1) {
                return DateFormat.getDateInstance(dateStyle, locale);
            }
            if (timeStyle != -1) {
                return DateFormat.getTimeInstance(timeStyle, locale);
            }
            throw new IllegalStateException("Unsupported style pattern '" + this.stylePattern + "'");
        }
    }
    
    private int getStylePatternForChar(final int index) {
        if (this.stylePattern != null && this.stylePattern.length() > index) {
            switch (this.stylePattern.charAt(index)) {
                case 'S': {
                    return 3;
                }
                case 'M': {
                    return 2;
                }
                case 'L': {
                    return 1;
                }
                case 'F': {
                    return 0;
                }
                case '-': {
                    return -1;
                }
            }
        }
        throw new IllegalStateException("Unsupported style pattern '" + this.stylePattern + "'");
    }
    
    static {
        final Map<DateTimeFormat.ISO, String> formats = new HashMap<DateTimeFormat.ISO, String>(4);
        formats.put(DateTimeFormat.ISO.DATE, "yyyy-MM-dd");
        formats.put(DateTimeFormat.ISO.TIME, "HH:mm:ss.SSSZ");
        formats.put(DateTimeFormat.ISO.DATE_TIME, "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        ISO_PATTERNS = Collections.unmodifiableMap((Map<? extends DateTimeFormat.ISO, ? extends String>)formats);
    }
}
