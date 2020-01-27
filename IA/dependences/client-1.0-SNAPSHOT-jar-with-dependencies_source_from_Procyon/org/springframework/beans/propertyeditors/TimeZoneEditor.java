// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import java.util.TimeZone;
import org.springframework.util.StringUtils;
import java.beans.PropertyEditorSupport;

public class TimeZoneEditor extends PropertyEditorSupport
{
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        this.setValue(StringUtils.parseTimeZoneString(text));
    }
    
    @Override
    public String getAsText() {
        final TimeZone value = (TimeZone)this.getValue();
        return (value != null) ? value.getID() : "";
    }
}
