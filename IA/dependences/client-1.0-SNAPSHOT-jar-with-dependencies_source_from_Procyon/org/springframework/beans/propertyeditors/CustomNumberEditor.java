// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import java.text.NumberFormat;
import java.beans.PropertyEditorSupport;

public class CustomNumberEditor extends PropertyEditorSupport
{
    private final Class<? extends Number> numberClass;
    private final NumberFormat numberFormat;
    private final boolean allowEmpty;
    
    public CustomNumberEditor(final Class<? extends Number> numberClass, final boolean allowEmpty) throws IllegalArgumentException {
        this(numberClass, null, allowEmpty);
    }
    
    public CustomNumberEditor(final Class<? extends Number> numberClass, final NumberFormat numberFormat, final boolean allowEmpty) throws IllegalArgumentException {
        if (numberClass == null || !Number.class.isAssignableFrom(numberClass)) {
            throw new IllegalArgumentException("Property class must be a subclass of Number");
        }
        this.numberClass = numberClass;
        this.numberFormat = numberFormat;
        this.allowEmpty = allowEmpty;
    }
    
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        if (this.allowEmpty && !StringUtils.hasText(text)) {
            this.setValue(null);
        }
        else if (this.numberFormat != null) {
            this.setValue(NumberUtils.parseNumber(text, (Class<Object>)this.numberClass, this.numberFormat));
        }
        else {
            this.setValue(NumberUtils.parseNumber(text, (Class<Object>)this.numberClass));
        }
    }
    
    @Override
    public void setValue(final Object value) {
        if (value instanceof Number) {
            super.setValue(NumberUtils.convertNumberToTargetClass((Number)value, (Class<Object>)this.numberClass));
        }
        else {
            super.setValue(value);
        }
    }
    
    @Override
    public String getAsText() {
        final Object value = this.getValue();
        if (value == null) {
            return "";
        }
        if (this.numberFormat != null) {
            return this.numberFormat.format(value);
        }
        return value.toString();
    }
}
