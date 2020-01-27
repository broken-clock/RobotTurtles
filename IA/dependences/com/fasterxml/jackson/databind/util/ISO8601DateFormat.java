// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.FieldPosition;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.DecimalFormat;
import java.text.DateFormat;

@Deprecated
public class ISO8601DateFormat extends DateFormat
{
    private static final long serialVersionUID = 1L;
    
    public ISO8601DateFormat() {
        this.numberFormat = new DecimalFormat();
        this.calendar = new GregorianCalendar();
    }
    
    @Override
    public StringBuffer format(final Date date, final StringBuffer toAppendTo, final FieldPosition fieldPosition) {
        toAppendTo.append(ISO8601Utils.format(date));
        return toAppendTo;
    }
    
    @Override
    public Date parse(final String source, final ParsePosition pos) {
        try {
            return ISO8601Utils.parse(source, pos);
        }
        catch (ParseException e) {
            return null;
        }
    }
    
    @Override
    public Date parse(final String source) throws ParseException {
        return ISO8601Utils.parse(source, new ParsePosition(0));
    }
    
    @Override
    public Object clone() {
        return this;
    }
}
