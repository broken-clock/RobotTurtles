// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import java.util.Currency;
import java.beans.PropertyEditorSupport;

public class CurrencyEditor extends PropertyEditorSupport
{
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        this.setValue(Currency.getInstance(text));
    }
    
    @Override
    public String getAsText() {
        final Currency value = (Currency)this.getValue();
        return (value != null) ? value.getCurrencyCode() : "";
    }
}
