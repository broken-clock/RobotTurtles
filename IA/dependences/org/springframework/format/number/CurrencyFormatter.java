// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.number;

import org.springframework.util.ClassUtils;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Currency;
import java.math.RoundingMode;

public class CurrencyFormatter extends AbstractNumberFormatter
{
    private static final boolean roundingModeOnDecimalFormat;
    private int fractionDigits;
    private RoundingMode roundingMode;
    private Currency currency;
    
    public CurrencyFormatter() {
        this.fractionDigits = 2;
    }
    
    public void setFractionDigits(final int fractionDigits) {
        this.fractionDigits = fractionDigits;
    }
    
    public void setRoundingMode(final RoundingMode roundingMode) {
        this.roundingMode = roundingMode;
    }
    
    public void setCurrency(final Currency currency) {
        this.currency = currency;
    }
    
    @Override
    public BigDecimal parse(final String text, final Locale locale) throws ParseException {
        BigDecimal decimal = (BigDecimal)super.parse(text, locale);
        if (decimal != null) {
            if (this.roundingMode != null) {
                decimal = decimal.setScale(this.fractionDigits, this.roundingMode);
            }
            else {
                decimal = decimal.setScale(this.fractionDigits);
            }
        }
        return decimal;
    }
    
    @Override
    protected NumberFormat getNumberFormat(final Locale locale) {
        final DecimalFormat format = (DecimalFormat)NumberFormat.getCurrencyInstance(locale);
        format.setParseBigDecimal(true);
        format.setMaximumFractionDigits(this.fractionDigits);
        format.setMinimumFractionDigits(this.fractionDigits);
        if (this.roundingMode != null && CurrencyFormatter.roundingModeOnDecimalFormat) {
            format.setRoundingMode(this.roundingMode);
        }
        if (this.currency != null) {
            format.setCurrency(this.currency);
        }
        return format;
    }
    
    static {
        roundingModeOnDecimalFormat = ClassUtils.hasMethod(DecimalFormat.class, "setRoundingMode", RoundingMode.class);
    }
}
