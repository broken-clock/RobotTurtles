// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.support;

import org.springframework.util.StringUtils;
import java.util.Iterator;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.BitSet;

public class CronSequenceGenerator
{
    private final BitSet seconds;
    private final BitSet minutes;
    private final BitSet hours;
    private final BitSet daysOfWeek;
    private final BitSet daysOfMonth;
    private final BitSet months;
    private final String expression;
    private final TimeZone timeZone;
    
    public CronSequenceGenerator(final String expression) {
        this(expression, TimeZone.getDefault());
    }
    
    public CronSequenceGenerator(final String expression, final TimeZone timeZone) {
        this.seconds = new BitSet(60);
        this.minutes = new BitSet(60);
        this.hours = new BitSet(24);
        this.daysOfWeek = new BitSet(7);
        this.daysOfMonth = new BitSet(31);
        this.months = new BitSet(12);
        this.expression = expression;
        this.timeZone = timeZone;
        this.parse(expression);
    }
    
    public Date next(final Date date) {
        final Calendar calendar = new GregorianCalendar();
        calendar.setTimeZone(this.timeZone);
        calendar.setTime(date);
        calendar.set(14, 0);
        final long originalTimestamp = calendar.getTimeInMillis();
        this.doNext(calendar, calendar.get(1));
        if (calendar.getTimeInMillis() == originalTimestamp) {
            calendar.add(13, 1);
            this.doNext(calendar, calendar.get(1));
        }
        return calendar.getTime();
    }
    
    private void doNext(final Calendar calendar, final int dot) {
        final List<Integer> resets = new ArrayList<Integer>();
        final int second = calendar.get(13);
        final List<Integer> emptyList = Collections.emptyList();
        final int updateSecond = this.findNext(this.seconds, second, calendar, 13, 12, emptyList);
        if (second == updateSecond) {
            resets.add(13);
        }
        final int minute = calendar.get(12);
        final int updateMinute = this.findNext(this.minutes, minute, calendar, 12, 11, resets);
        if (minute == updateMinute) {
            resets.add(12);
        }
        else {
            this.doNext(calendar, dot);
        }
        final int hour = calendar.get(11);
        final int updateHour = this.findNext(this.hours, hour, calendar, 11, 7, resets);
        if (hour == updateHour) {
            resets.add(11);
        }
        else {
            this.doNext(calendar, dot);
        }
        final int dayOfWeek = calendar.get(7);
        final int dayOfMonth = calendar.get(5);
        final int updateDayOfMonth = this.findNextDay(calendar, this.daysOfMonth, dayOfMonth, this.daysOfWeek, dayOfWeek, resets);
        if (dayOfMonth == updateDayOfMonth) {
            resets.add(5);
        }
        else {
            this.doNext(calendar, dot);
        }
        final int month = calendar.get(2);
        final int updateMonth = this.findNext(this.months, month, calendar, 2, 1, resets);
        if (month != updateMonth) {
            if (calendar.get(1) - dot > 4) {
                throw new IllegalArgumentException("Invalid cron expression \"" + this.expression + "\" led to runaway search for next trigger");
            }
            this.doNext(calendar, dot);
        }
    }
    
    private int findNextDay(final Calendar calendar, final BitSet daysOfMonth, int dayOfMonth, final BitSet daysOfWeek, int dayOfWeek, final List<Integer> resets) {
        int count = 0;
        final int max = 366;
        while ((!daysOfMonth.get(dayOfMonth) || !daysOfWeek.get(dayOfWeek - 1)) && count++ < max) {
            calendar.add(5, 1);
            dayOfMonth = calendar.get(5);
            dayOfWeek = calendar.get(7);
            this.reset(calendar, resets);
        }
        if (count >= max) {
            throw new IllegalArgumentException("Overflow in day for expression \"" + this.expression + "\"");
        }
        return dayOfMonth;
    }
    
    private int findNext(final BitSet bits, final int value, final Calendar calendar, final int field, final int nextField, final List<Integer> lowerOrders) {
        int nextValue = bits.nextSetBit(value);
        if (nextValue == -1) {
            calendar.add(nextField, 1);
            this.reset(calendar, Arrays.asList(field));
            nextValue = bits.nextSetBit(0);
        }
        if (nextValue != value) {
            calendar.set(field, nextValue);
            this.reset(calendar, lowerOrders);
        }
        return nextValue;
    }
    
    private void reset(final Calendar calendar, final List<Integer> fields) {
        for (final int field : fields) {
            calendar.set(field, (field == 5) ? 1 : 0);
        }
    }
    
    private void parse(final String expression) throws IllegalArgumentException {
        final String[] fields = StringUtils.tokenizeToStringArray(expression, " ");
        if (fields.length != 6) {
            throw new IllegalArgumentException(String.format("Cron expression must consist of 6 fields (found %d in \"%s\")", fields.length, expression));
        }
        this.setNumberHits(this.seconds, fields[0], 0, 60);
        this.setNumberHits(this.minutes, fields[1], 0, 60);
        this.setNumberHits(this.hours, fields[2], 0, 24);
        this.setDaysOfMonth(this.daysOfMonth, fields[3]);
        this.setMonths(this.months, fields[4]);
        this.setDays(this.daysOfWeek, this.replaceOrdinals(fields[5], "SUN,MON,TUE,WED,THU,FRI,SAT"), 8);
        if (this.daysOfWeek.get(7)) {
            this.daysOfWeek.set(0);
            this.daysOfWeek.clear(7);
        }
    }
    
    private String replaceOrdinals(String value, final String commaSeparatedList) {
        final String[] list = StringUtils.commaDelimitedListToStringArray(commaSeparatedList);
        for (int i = 0; i < list.length; ++i) {
            final String item = list[i].toUpperCase();
            value = StringUtils.replace(value.toUpperCase(), item, "" + i);
        }
        return value;
    }
    
    private void setDaysOfMonth(final BitSet bits, final String field) {
        final int max = 31;
        this.setDays(bits, field, max + 1);
        bits.clear(0);
    }
    
    private void setDays(final BitSet bits, String field, final int max) {
        if (field.contains("?")) {
            field = "*";
        }
        this.setNumberHits(bits, field, 0, max);
    }
    
    private void setMonths(final BitSet bits, String value) {
        final int max = 12;
        value = this.replaceOrdinals(value, "FOO,JAN,FEB,MAR,APR,MAY,JUN,JUL,AUG,SEP,OCT,NOV,DEC");
        final BitSet months = new BitSet(13);
        this.setNumberHits(months, value, 1, max + 1);
        for (int i = 1; i <= max; ++i) {
            if (months.get(i)) {
                bits.set(i - 1);
            }
        }
    }
    
    private void setNumberHits(final BitSet bits, final String value, final int min, final int max) {
        final String[] delimitedListToStringArray;
        final String[] fields = delimitedListToStringArray = StringUtils.delimitedListToStringArray(value, ",");
        for (final String field : delimitedListToStringArray) {
            if (!field.contains("/")) {
                final int[] range = this.getRange(field, min, max);
                bits.set(range[0], range[1] + 1);
            }
            else {
                final String[] split = StringUtils.delimitedListToStringArray(field, "/");
                if (split.length > 2) {
                    throw new IllegalArgumentException("Incrementer has more than two fields: '" + field + "' in expression \"" + this.expression + "\"");
                }
                final int[] range2 = this.getRange(split[0], min, max);
                if (!split[0].contains("-")) {
                    range2[1] = max - 1;
                }
                for (int delta = Integer.valueOf(split[1]), i = range2[0]; i <= range2[1]; i += delta) {
                    bits.set(i);
                }
            }
        }
    }
    
    private int[] getRange(final String field, final int min, final int max) {
        final int[] result = new int[2];
        if (field.contains("*")) {
            result[0] = min;
            result[1] = max - 1;
            return result;
        }
        if (!field.contains("-")) {
            result[0] = (result[1] = Integer.valueOf(field));
        }
        else {
            final String[] split = StringUtils.delimitedListToStringArray(field, "-");
            if (split.length > 2) {
                throw new IllegalArgumentException("Range has more than two fields: '" + field + "' in expression \"" + this.expression + "\"");
            }
            result[0] = Integer.valueOf(split[0]);
            result[1] = Integer.valueOf(split[1]);
        }
        if (result[0] >= max || result[1] >= max) {
            throw new IllegalArgumentException("Range exceeds maximum (" + max + "): '" + field + "' in expression \"" + this.expression + "\"");
        }
        if (result[0] < min || result[1] < min) {
            throw new IllegalArgumentException("Range less than minimum (" + min + "): '" + field + "' in expression \"" + this.expression + "\"");
        }
        return result;
    }
    
    String getExpression() {
        return this.expression;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof CronSequenceGenerator)) {
            return false;
        }
        final CronSequenceGenerator cron = (CronSequenceGenerator)obj;
        return cron.months.equals(this.months) && cron.daysOfMonth.equals(this.daysOfMonth) && cron.daysOfWeek.equals(this.daysOfWeek) && cron.hours.equals(this.hours) && cron.minutes.equals(this.minutes) && cron.seconds.equals(this.seconds);
    }
    
    @Override
    public int hashCode() {
        return 37 + 17 * this.months.hashCode() + 29 * this.daysOfMonth.hashCode() + 37 * this.daysOfWeek.hashCode() + 41 * this.hours.hashCode() + 53 * this.minutes.hashCode() + 61 * this.seconds.hashCode();
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.expression;
    }
}
