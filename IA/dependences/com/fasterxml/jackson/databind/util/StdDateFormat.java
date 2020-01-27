// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.text.FieldPosition;
import com.fasterxml.jackson.core.io.NumberInput;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.text.DateFormat;

public class StdDateFormat extends DateFormat
{
    protected static final String PATTERN_PLAIN_STR = "\\d\\d\\d\\d[-]\\d\\d[-]\\d\\d";
    protected static final Pattern PATTERN_PLAIN;
    protected static final Pattern PATTERN_ISO8601;
    public static final String DATE_FORMAT_STR_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    protected static final String DATE_FORMAT_STR_PLAIN = "yyyy-MM-dd";
    protected static final String DATE_FORMAT_STR_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
    protected static final String[] ALL_FORMATS;
    protected static final TimeZone DEFAULT_TIMEZONE;
    protected static final Locale DEFAULT_LOCALE;
    protected static final DateFormat DATE_FORMAT_RFC1123;
    protected static final DateFormat DATE_FORMAT_ISO8601;
    public static final StdDateFormat instance;
    protected static final Calendar CALENDAR;
    protected transient TimeZone _timezone;
    protected final Locale _locale;
    protected Boolean _lenient;
    private transient Calendar _calendar;
    private transient DateFormat _formatRFC1123;
    private boolean _tzSerializedWithColon;
    
    public StdDateFormat() {
        this._tzSerializedWithColon = false;
        this._locale = StdDateFormat.DEFAULT_LOCALE;
    }
    
    @Deprecated
    public StdDateFormat(final TimeZone tz, final Locale loc) {
        this._tzSerializedWithColon = false;
        this._timezone = tz;
        this._locale = loc;
    }
    
    protected StdDateFormat(final TimeZone tz, final Locale loc, final Boolean lenient) {
        this(tz, loc, lenient, false);
    }
    
    protected StdDateFormat(final TimeZone tz, final Locale loc, final Boolean lenient, final boolean formatTzOffsetWithColon) {
        this._tzSerializedWithColon = false;
        this._timezone = tz;
        this._locale = loc;
        this._lenient = lenient;
        this._tzSerializedWithColon = formatTzOffsetWithColon;
    }
    
    public static TimeZone getDefaultTimeZone() {
        return StdDateFormat.DEFAULT_TIMEZONE;
    }
    
    public StdDateFormat withTimeZone(TimeZone tz) {
        if (tz == null) {
            tz = StdDateFormat.DEFAULT_TIMEZONE;
        }
        if (tz == this._timezone || tz.equals(this._timezone)) {
            return this;
        }
        return new StdDateFormat(tz, this._locale, this._lenient, this._tzSerializedWithColon);
    }
    
    public StdDateFormat withLocale(final Locale loc) {
        if (loc.equals(this._locale)) {
            return this;
        }
        return new StdDateFormat(this._timezone, loc, this._lenient, this._tzSerializedWithColon);
    }
    
    public StdDateFormat withLenient(final Boolean b) {
        if (_equals(b, this._lenient)) {
            return this;
        }
        return new StdDateFormat(this._timezone, this._locale, b, this._tzSerializedWithColon);
    }
    
    public StdDateFormat withColonInTimeZone(final boolean b) {
        if (this._tzSerializedWithColon == b) {
            return this;
        }
        return new StdDateFormat(this._timezone, this._locale, this._lenient, b);
    }
    
    @Override
    public StdDateFormat clone() {
        return new StdDateFormat(this._timezone, this._locale, this._lenient, this._tzSerializedWithColon);
    }
    
    @Deprecated
    public static DateFormat getISO8601Format(final TimeZone tz, final Locale loc) {
        return _cloneFormat(StdDateFormat.DATE_FORMAT_ISO8601, "yyyy-MM-dd'T'HH:mm:ss.SSSZ", tz, loc, null);
    }
    
    @Deprecated
    public static DateFormat getRFC1123Format(final TimeZone tz, final Locale loc) {
        return _cloneFormat(StdDateFormat.DATE_FORMAT_RFC1123, "EEE, dd MMM yyyy HH:mm:ss zzz", tz, loc, null);
    }
    
    @Override
    public TimeZone getTimeZone() {
        return this._timezone;
    }
    
    @Override
    public void setTimeZone(final TimeZone tz) {
        if (!tz.equals(this._timezone)) {
            this._clearFormats();
            this._timezone = tz;
        }
    }
    
    @Override
    public void setLenient(final boolean enabled) {
        final Boolean newValue = enabled;
        if (!_equals(newValue, this._lenient)) {
            this._lenient = newValue;
            this._clearFormats();
        }
    }
    
    @Override
    public boolean isLenient() {
        return this._lenient == null || this._lenient;
    }
    
    public boolean isColonIncludedInTimeZone() {
        return this._tzSerializedWithColon;
    }
    
    @Override
    public Date parse(String dateStr) throws ParseException {
        dateStr = dateStr.trim();
        final ParsePosition pos = new ParsePosition(0);
        final Date dt = this._parseDate(dateStr, pos);
        if (dt != null) {
            return dt;
        }
        final StringBuilder sb = new StringBuilder();
        for (final String f : StdDateFormat.ALL_FORMATS) {
            if (sb.length() > 0) {
                sb.append("\", \"");
            }
            else {
                sb.append('\"');
            }
            sb.append(f);
        }
        sb.append('\"');
        throw new ParseException(String.format("Cannot parse date \"%s\": not compatible with any of standard forms (%s)", dateStr, sb.toString()), pos.getErrorIndex());
    }
    
    @Override
    public Date parse(final String dateStr, final ParsePosition pos) {
        try {
            return this._parseDate(dateStr, pos);
        }
        catch (ParseException ex) {
            return null;
        }
    }
    
    protected Date _parseDate(final String dateStr, final ParsePosition pos) throws ParseException {
        if (this.looksLikeISO8601(dateStr)) {
            return this.parseAsISO8601(dateStr, pos);
        }
        int i = dateStr.length();
        while (--i >= 0) {
            final char ch = dateStr.charAt(i);
            if (ch < '0' || ch > '9') {
                if (i > 0) {
                    break;
                }
                if (ch != '-') {
                    break;
                }
                continue;
            }
        }
        if (i < 0 && (dateStr.charAt(0) == '-' || NumberInput.inLongRange(dateStr, false))) {
            return this._parseDateFromLong(dateStr, pos);
        }
        return this.parseAsRFC1123(dateStr, pos);
    }
    
    @Override
    public StringBuffer format(final Date date, final StringBuffer toAppendTo, final FieldPosition fieldPosition) {
        TimeZone tz = this._timezone;
        if (tz == null) {
            tz = StdDateFormat.DEFAULT_TIMEZONE;
        }
        this._format(tz, this._locale, date, toAppendTo);
        return toAppendTo;
    }
    
    protected void _format(final TimeZone tz, final Locale loc, final Date date, final StringBuffer buffer) {
        final Calendar cal = this._getCalendar(tz);
        cal.setTime(date);
        final int year = cal.get(1);
        if (cal.get(0) == 0) {
            this._formatBCEYear(buffer, year);
        }
        else {
            if (year > 9999) {
                buffer.append('+');
            }
            pad4(buffer, year);
        }
        buffer.append('-');
        pad2(buffer, cal.get(2) + 1);
        buffer.append('-');
        pad2(buffer, cal.get(5));
        buffer.append('T');
        pad2(buffer, cal.get(11));
        buffer.append(':');
        pad2(buffer, cal.get(12));
        buffer.append(':');
        pad2(buffer, cal.get(13));
        buffer.append('.');
        pad3(buffer, cal.get(14));
        final int offset = tz.getOffset(cal.getTimeInMillis());
        if (offset != 0) {
            final int hours = Math.abs(offset / 60000 / 60);
            final int minutes = Math.abs(offset / 60000 % 60);
            buffer.append((offset < 0) ? '-' : '+');
            pad2(buffer, hours);
            if (this._tzSerializedWithColon) {
                buffer.append(':');
            }
            pad2(buffer, minutes);
        }
        else if (this._tzSerializedWithColon) {
            buffer.append("+00:00");
        }
        else {
            buffer.append("+0000");
        }
    }
    
    protected void _formatBCEYear(final StringBuffer buffer, final int bceYearNoSign) {
        if (bceYearNoSign == 1) {
            buffer.append("+0000");
            return;
        }
        final int isoYear = bceYearNoSign - 1;
        buffer.append('-');
        pad4(buffer, isoYear);
    }
    
    private static void pad2(final StringBuffer buffer, int value) {
        final int tens = value / 10;
        if (tens == 0) {
            buffer.append('0');
        }
        else {
            buffer.append((char)(48 + tens));
            value -= 10 * tens;
        }
        buffer.append((char)(48 + value));
    }
    
    private static void pad3(final StringBuffer buffer, int value) {
        final int h = value / 100;
        if (h == 0) {
            buffer.append('0');
        }
        else {
            buffer.append((char)(48 + h));
            value -= h * 100;
        }
        pad2(buffer, value);
    }
    
    private static void pad4(final StringBuffer buffer, int value) {
        final int h = value / 100;
        if (h == 0) {
            buffer.append('0').append('0');
        }
        else {
            if (h > 99) {
                buffer.append(h);
            }
            else {
                pad2(buffer, h);
            }
            value -= 100 * h;
        }
        pad2(buffer, value);
    }
    
    @Override
    public String toString() {
        return String.format("DateFormat %s: (timezone: %s, locale: %s, lenient: %s)", this.getClass().getName(), this._timezone, this._locale, this._lenient);
    }
    
    public String toPattern() {
        final StringBuilder sb = new StringBuilder(100);
        sb.append("[one of: '").append("yyyy-MM-dd'T'HH:mm:ss.SSSZ").append("', '").append("EEE, dd MMM yyyy HH:mm:ss zzz").append("' (");
        sb.append(Boolean.FALSE.equals(this._lenient) ? "strict" : "lenient").append(")]");
        return sb.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this;
    }
    
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
    
    protected boolean looksLikeISO8601(final String dateStr) {
        return dateStr.length() >= 7 && Character.isDigit(dateStr.charAt(0)) && Character.isDigit(dateStr.charAt(3)) && dateStr.charAt(4) == '-' && Character.isDigit(dateStr.charAt(5));
    }
    
    private Date _parseDateFromLong(final String longStr, final ParsePosition pos) throws ParseException {
        long ts;
        try {
            ts = NumberInput.parseLong(longStr);
        }
        catch (NumberFormatException e) {
            throw new ParseException(String.format("Timestamp value %s out of 64-bit value range", longStr), pos.getErrorIndex());
        }
        return new Date(ts);
    }
    
    protected Date parseAsISO8601(final String dateStr, final ParsePosition pos) throws ParseException {
        try {
            return this._parseAsISO8601(dateStr, pos);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(String.format("Cannot parse date \"%s\", problem: %s", dateStr, e.getMessage()), pos.getErrorIndex());
        }
    }
    
    protected Date _parseAsISO8601(final String dateStr, final ParsePosition bogus) throws IllegalArgumentException, ParseException {
        final int totalLen = dateStr.length();
        TimeZone tz = StdDateFormat.DEFAULT_TIMEZONE;
        if (this._timezone != null && 'Z' != dateStr.charAt(totalLen - 1)) {
            tz = this._timezone;
        }
        final Calendar cal = this._getCalendar(tz);
        cal.clear();
        String formatStr;
        if (totalLen <= 10) {
            final Matcher m = StdDateFormat.PATTERN_PLAIN.matcher(dateStr);
            if (m.matches()) {
                final int year = _parse4D(dateStr, 0);
                final int month = _parse2D(dateStr, 5) - 1;
                final int day = _parse2D(dateStr, 8);
                cal.set(year, month, day, 0, 0, 0);
                cal.set(14, 0);
                return cal.getTime();
            }
            formatStr = "yyyy-MM-dd";
        }
        else {
            final Matcher m = StdDateFormat.PATTERN_ISO8601.matcher(dateStr);
            if (m.matches()) {
                int start = m.start(2);
                int end = m.end(2);
                final int len = end - start;
                if (len > 1) {
                    int offsetSecs = _parse2D(dateStr, start + 1) * 3600;
                    if (len >= 5) {
                        offsetSecs += _parse2D(dateStr, end - 2) * 60;
                    }
                    if (dateStr.charAt(start) == '-') {
                        offsetSecs *= -1000;
                    }
                    else {
                        offsetSecs *= 1000;
                    }
                    cal.set(15, offsetSecs);
                    cal.set(16, 0);
                }
                final int year2 = _parse4D(dateStr, 0);
                final int month2 = _parse2D(dateStr, 5) - 1;
                final int day2 = _parse2D(dateStr, 8);
                final int hour = _parse2D(dateStr, 11);
                final int minute = _parse2D(dateStr, 14);
                int seconds;
                if (totalLen > 16 && dateStr.charAt(16) == ':') {
                    seconds = _parse2D(dateStr, 17);
                }
                else {
                    seconds = 0;
                }
                cal.set(year2, month2, day2, hour, minute, seconds);
                start = m.start(1) + 1;
                end = m.end(1);
                int msecs = 0;
                if (start >= end) {
                    cal.set(14, 0);
                }
                else {
                    msecs = 0;
                    final int fractLen = end - start;
                    switch (fractLen) {
                        default: {
                            if (fractLen > 9) {
                                throw new ParseException(String.format("Cannot parse date \"%s\": invalid fractional seconds '%s'; can use at most 9 digits", dateStr, m.group(1).substring(1)), start);
                            }
                        }
                        case 3: {
                            msecs += dateStr.charAt(start + 2) - '0';
                        }
                        case 2: {
                            msecs += 10 * (dateStr.charAt(start + 1) - '0');
                        }
                        case 1: {
                            msecs += 100 * (dateStr.charAt(start) - '0');
                        }
                        case 0: {
                            cal.set(14, msecs);
                            break;
                        }
                    }
                }
                return cal.getTime();
            }
            formatStr = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        }
        throw new ParseException(String.format("Cannot parse date \"%s\": while it seems to fit format '%s', parsing fails (leniency? %s)", dateStr, formatStr, this._lenient), 0);
    }
    
    private static int _parse4D(final String str, final int index) {
        return 1000 * (str.charAt(index) - '0') + 100 * (str.charAt(index + 1) - '0') + 10 * (str.charAt(index + 2) - '0') + (str.charAt(index + 3) - '0');
    }
    
    private static int _parse2D(final String str, final int index) {
        return 10 * (str.charAt(index) - '0') + (str.charAt(index + 1) - '0');
    }
    
    protected Date parseAsRFC1123(final String dateStr, final ParsePosition pos) {
        if (this._formatRFC1123 == null) {
            this._formatRFC1123 = _cloneFormat(StdDateFormat.DATE_FORMAT_RFC1123, "EEE, dd MMM yyyy HH:mm:ss zzz", this._timezone, this._locale, this._lenient);
        }
        return this._formatRFC1123.parse(dateStr, pos);
    }
    
    private static final DateFormat _cloneFormat(DateFormat df, final String format, final TimeZone tz, final Locale loc, final Boolean lenient) {
        if (!loc.equals(StdDateFormat.DEFAULT_LOCALE)) {
            df = new SimpleDateFormat(format, loc);
            df.setTimeZone((tz == null) ? StdDateFormat.DEFAULT_TIMEZONE : tz);
        }
        else {
            df = (DateFormat)df.clone();
            if (tz != null) {
                df.setTimeZone(tz);
            }
        }
        if (lenient != null) {
            df.setLenient(lenient);
        }
        return df;
    }
    
    protected void _clearFormats() {
        this._formatRFC1123 = null;
    }
    
    protected Calendar _getCalendar(final TimeZone tz) {
        Calendar cal = this._calendar;
        if (cal == null) {
            cal = (this._calendar = (Calendar)StdDateFormat.CALENDAR.clone());
        }
        if (!cal.getTimeZone().equals(tz)) {
            cal.setTimeZone(tz);
        }
        cal.setLenient(this.isLenient());
        return cal;
    }
    
    protected static <T> boolean _equals(final T value1, final T value2) {
        return value1 == value2 || (value1 != null && value1.equals(value2));
    }
    
    static {
        PATTERN_PLAIN = Pattern.compile("\\d\\d\\d\\d[-]\\d\\d[-]\\d\\d");
        Pattern p = null;
        try {
            p = Pattern.compile("\\d\\d\\d\\d[-]\\d\\d[-]\\d\\d[T]\\d\\d[:]\\d\\d(?:[:]\\d\\d)?(\\.\\d+)?(Z|[+-]\\d\\d(?:[:]?\\d\\d)?)?");
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
        PATTERN_ISO8601 = p;
        ALL_FORMATS = new String[] { "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ss.SSS", "EEE, dd MMM yyyy HH:mm:ss zzz", "yyyy-MM-dd" };
        DEFAULT_TIMEZONE = TimeZone.getTimeZone("UTC");
        DEFAULT_LOCALE = Locale.US;
        (DATE_FORMAT_RFC1123 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", StdDateFormat.DEFAULT_LOCALE)).setTimeZone(StdDateFormat.DEFAULT_TIMEZONE);
        (DATE_FORMAT_ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", StdDateFormat.DEFAULT_LOCALE)).setTimeZone(StdDateFormat.DEFAULT_TIMEZONE);
        instance = new StdDateFormat();
        CALENDAR = new GregorianCalendar(StdDateFormat.DEFAULT_TIMEZONE, StdDateFormat.DEFAULT_LOCALE);
    }
}
