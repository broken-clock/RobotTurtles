// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import org.springframework.util.StringUtils;
import java.beans.PropertyEditorSupport;

public class StringTrimmerEditor extends PropertyEditorSupport
{
    private final String charsToDelete;
    private final boolean emptyAsNull;
    
    public StringTrimmerEditor(final boolean emptyAsNull) {
        this.charsToDelete = null;
        this.emptyAsNull = emptyAsNull;
    }
    
    public StringTrimmerEditor(final String charsToDelete, final boolean emptyAsNull) {
        this.charsToDelete = charsToDelete;
        this.emptyAsNull = emptyAsNull;
    }
    
    @Override
    public void setAsText(final String text) {
        if (text == null) {
            this.setValue(null);
        }
        else {
            String value = text.trim();
            if (this.charsToDelete != null) {
                value = StringUtils.deleteAny(value, this.charsToDelete);
            }
            if (this.emptyAsNull && "".equals(value)) {
                this.setValue(null);
            }
            else {
                this.setValue(value);
            }
        }
    }
    
    @Override
    public String getAsText() {
        final Object value = this.getValue();
        return (value != null) ? value.toString() : "";
    }
}
