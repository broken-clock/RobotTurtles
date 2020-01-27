// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import java.util.Date;
import java.text.ParseException;
import org.springframework.util.StringUtils;
import java.text.DateFormat;
import java.beans.PropertyEditorSupport;

public class CustomDateEditor extends PropertyEditorSupport
{
    private final DateFormat dateFormat;
    private final boolean allowEmpty;
    private final int exactDateLength;
    
    public CustomDateEditor(final DateFormat dateFormat, final boolean allowEmpty) {
        this.dateFormat = dateFormat;
        this.allowEmpty = allowEmpty;
        this.exactDateLength = -1;
    }
    
    public CustomDateEditor(final DateFormat dateFormat, final boolean allowEmpty, final int exactDateLength) {
        this.dateFormat = dateFormat;
        this.allowEmpty = allowEmpty;
        this.exactDateLength = exactDateLength;
    }
    
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        if (this.allowEmpty && !StringUtils.hasText(text)) {
            this.setValue(null);
        }
        else {
            if (text != null && this.exactDateLength >= 0 && text.length() != this.exactDateLength) {
                throw new IllegalArgumentException("Could not parse date: it is not exactly" + this.exactDateLength + "characters long");
            }
            try {
                this.setValue(this.dateFormat.parse(text));
            }
            catch (ParseException ex) {
                throw new IllegalArgumentException("Could not parse date: " + ex.getMessage(), ex);
            }
        }
    }
    
    @Override
    public String getAsText() {
        final Date value = (Date)this.getValue();
        return (value != null) ? this.dateFormat.format(value) : "";
    }
}
