// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.number;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class PercentFormatter extends AbstractNumberFormatter
{
    @Override
    protected NumberFormat getNumberFormat(final Locale locale) {
        final NumberFormat format = NumberFormat.getPercentInstance(locale);
        if (format instanceof DecimalFormat) {
            ((DecimalFormat)format).setParseBigDecimal(true);
        }
        return format;
    }
}
