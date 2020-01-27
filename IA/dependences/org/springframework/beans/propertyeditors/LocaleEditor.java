// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import org.springframework.util.StringUtils;
import java.beans.PropertyEditorSupport;

public class LocaleEditor extends PropertyEditorSupport
{
    @Override
    public void setAsText(final String text) {
        this.setValue(StringUtils.parseLocaleString(text));
    }
    
    @Override
    public String getAsText() {
        final Object value = this.getValue();
        return (value != null) ? value.toString() : "";
    }
}
