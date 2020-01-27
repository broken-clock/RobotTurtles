// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.number;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberFormatter extends AbstractNumberFormatter
{
    private String pattern;
    
    public NumberFormatter() {
    }
    
    public NumberFormatter(final String pattern) {
        this.pattern = pattern;
    }
    
    public void setPattern(final String pattern) {
        this.pattern = pattern;
    }
    
    public NumberFormat getNumberFormat(final Locale locale) {
        final NumberFormat format = NumberFormat.getInstance(locale);
        if (format instanceof DecimalFormat) {
            final DecimalFormat decimalFormat = (DecimalFormat)format;
            decimalFormat.setParseBigDecimal(true);
            if (this.pattern != null) {
                decimalFormat.applyPattern(this.pattern);
            }
            return decimalFormat;
        }
        if (this.pattern != null) {
            throw new IllegalStateException("Cannot support pattern for non-DecimalFormat: " + format);
        }
        return format;
    }
}
