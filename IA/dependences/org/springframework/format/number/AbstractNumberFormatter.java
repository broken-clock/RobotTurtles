// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.number;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;
import org.springframework.format.Formatter;

public abstract class AbstractNumberFormatter implements Formatter<Number>
{
    private boolean lenient;
    
    public AbstractNumberFormatter() {
        this.lenient = false;
    }
    
    public void setLenient(final boolean lenient) {
        this.lenient = lenient;
    }
    
    @Override
    public String print(final Number number, final Locale locale) {
        return this.getNumberFormat(locale).format(number);
    }
    
    @Override
    public Number parse(final String text, final Locale locale) throws ParseException {
        final NumberFormat format = this.getNumberFormat(locale);
        final ParsePosition position = new ParsePosition(0);
        final Number number = format.parse(text, position);
        if (position.getErrorIndex() != -1) {
            throw new ParseException(text, position.getIndex());
        }
        if (!this.lenient && text.length() != position.getIndex()) {
            throw new ParseException(text, position.getIndex());
        }
        return number;
    }
    
    protected abstract NumberFormat getNumberFormat(final Locale p0);
}
