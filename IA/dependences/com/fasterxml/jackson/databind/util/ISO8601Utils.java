// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Date;
import java.util.TimeZone;

@Deprecated
public class ISO8601Utils
{
    protected static final int DEF_8601_LEN;
    private static final TimeZone TIMEZONE_Z;
    
    public static String format(final Date date) {
        return format(date, false, ISO8601Utils.TIMEZONE_Z);
    }
    
    public static String format(final Date date, final boolean millis) {
        return format(date, millis, ISO8601Utils.TIMEZONE_Z);
    }
    
    @Deprecated
    public static String format(final Date date, final boolean millis, final TimeZone tz) {
        return format(date, millis, tz, Locale.US);
    }
    
    public static String format(final Date date, final boolean millis, final TimeZone tz, final Locale loc) {
        final Calendar calendar = new GregorianCalendar(tz, loc);
        calendar.setTime(date);
        final StringBuilder sb = new StringBuilder(30);
        sb.append(String.format("%04d-%02d-%02dT%02d:%02d:%02d", calendar.get(1), calendar.get(2) + 1, calendar.get(5), calendar.get(11), calendar.get(12), calendar.get(13)));
        if (millis) {
            sb.append(String.format(".%03d", calendar.get(14)));
        }
        final int offset = tz.getOffset(calendar.getTimeInMillis());
        if (offset != 0) {
            final int hours = Math.abs(offset / 60000 / 60);
            final int minutes = Math.abs(offset / 60000 % 60);
            sb.append(String.format("%c%02d:%02d", (offset < 0) ? '-' : '+', hours, minutes));
        }
        else {
            sb.append('Z');
        }
        return sb.toString();
    }
    
    public static Date parse(final String date, final ParsePosition pos) throws ParseException {
        Exception fail = null;
        try {
            final int index;
            int offset = index = pos.getIndex();
            offset += 4;
            final int year = parseInt(date, index, offset);
            if (checkOffset(date, offset, '-')) {
                ++offset;
            }
            final int beginIndex = offset;
            offset += 2;
            final int month = parseInt(date, beginIndex, offset);
            if (checkOffset(date, offset, '-')) {
                ++offset;
            }
            final int beginIndex2 = offset;
            offset += 2;
            final int day = parseInt(date, beginIndex2, offset);
            int hour = 0;
            int minutes = 0;
            int seconds = 0;
            int milliseconds = 0;
            final boolean hasT = checkOffset(date, offset, 'T');
            if (!hasT && date.length() <= offset) {
                final Calendar calendar = new GregorianCalendar(year, month - 1, day);
                pos.setIndex(offset);
                return calendar.getTime();
            }
            if (hasT) {
                final int beginIndex3 = ++offset;
                offset += 2;
                hour = parseInt(date, beginIndex3, offset);
                if (checkOffset(date, offset, ':')) {
                    ++offset;
                }
                final int beginIndex4 = offset;
                offset += 2;
                minutes = parseInt(date, beginIndex4, offset);
                if (checkOffset(date, offset, ':')) {
                    ++offset;
                }
                if (date.length() > offset) {
                    final char c = date.charAt(offset);
                    if (c != 'Z' && c != '+' && c != '-') {
                        final int beginIndex5 = offset;
                        offset += 2;
                        seconds = parseInt(date, beginIndex5, offset);
                        if (seconds > 59 && seconds < 63) {
                            seconds = 59;
                        }
                        if (checkOffset(date, offset, '.')) {
                            ++offset;
                            final int endOffset = indexOfNonDigit(date, offset + 1);
                            final int parseEndOffset = Math.min(endOffset, offset + 3);
                            final int fraction = parseInt(date, offset, parseEndOffset);
                            switch (parseEndOffset - offset) {
                                case 2: {
                                    milliseconds = fraction * 10;
                                    break;
                                }
                                case 1: {
                                    milliseconds = fraction * 100;
                                    break;
                                }
                                default: {
                                    milliseconds = fraction;
                                    break;
                                }
                            }
                            offset = endOffset;
                        }
                    }
                }
            }
            if (date.length() <= offset) {
                throw new IllegalArgumentException("No time zone indicator");
            }
            TimeZone timezone = null;
            final char timezoneIndicator = date.charAt(offset);
            if (timezoneIndicator == 'Z') {
                timezone = ISO8601Utils.TIMEZONE_Z;
                ++offset;
            }
            else {
                if (timezoneIndicator != '+' && timezoneIndicator != '-') {
                    throw new IndexOutOfBoundsException("Invalid time zone indicator '" + timezoneIndicator + "'");
                }
                final String timezoneOffset = date.substring(offset);
                offset += timezoneOffset.length();
                if ("+0000".equals(timezoneOffset) || "+00:00".equals(timezoneOffset)) {
                    timezone = ISO8601Utils.TIMEZONE_Z;
                }
                else {
                    final String timezoneId = "GMT" + timezoneOffset;
                    timezone = TimeZone.getTimeZone(timezoneId);
                    final String act = timezone.getID();
                    if (!act.equals(timezoneId)) {
                        final String cleaned = act.replace(":", "");
                        if (!cleaned.equals(timezoneId)) {
                            throw new IndexOutOfBoundsException("Mismatching time zone indicator: " + timezoneId + " given, resolves to " + timezone.getID());
                        }
                    }
                }
            }
            final Calendar calendar2 = new GregorianCalendar(timezone);
            calendar2.setLenient(false);
            calendar2.set(1, year);
            calendar2.set(2, month - 1);
            calendar2.set(5, day);
            calendar2.set(11, hour);
            calendar2.set(12, minutes);
            calendar2.set(13, seconds);
            calendar2.set(14, milliseconds);
            pos.setIndex(offset);
            return calendar2.getTime();
        }
        catch (Exception e) {
            fail = e;
            final String input = (date == null) ? null : ('\"' + date + '\"');
            String msg = fail.getMessage();
            if (msg == null || msg.isEmpty()) {
                msg = "(" + fail.getClass().getName() + ")";
            }
            final ParseException ex = new ParseException("Failed to parse date " + input + ": " + msg, pos.getIndex());
            ex.initCause(fail);
            throw ex;
        }
    }
    
    private static boolean checkOffset(final String value, final int offset, final char expected) {
        return offset < value.length() && value.charAt(offset) == expected;
    }
    
    private static int parseInt(final String value, final int beginIndex, final int endIndex) throws NumberFormatException {
        if (beginIndex < 0 || endIndex > value.length() || beginIndex > endIndex) {
            throw new NumberFormatException(value);
        }
        int i = beginIndex;
        int result = 0;
        if (i < endIndex) {
            final int digit = Character.digit(value.charAt(i++), 10);
            if (digit < 0) {
                throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex));
            }
            result = -digit;
        }
        while (i < endIndex) {
            final int digit = Character.digit(value.charAt(i++), 10);
            if (digit < 0) {
                throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex));
            }
            result *= 10;
            result -= digit;
        }
        return -result;
    }
    
    private static int indexOfNonDigit(final String string, final int offset) {
        for (int i = offset; i < string.length(); ++i) {
            final char c = string.charAt(i);
            if (c < '0' || c > '9') {
                return i;
            }
        }
        return string.length();
    }
    
    static {
        DEF_8601_LEN = "yyyy-MM-ddThh:mm:ss.SSS+00:00".length();
        TIMEZONE_Z = TimeZone.getTimeZone("UTC");
    }
}
