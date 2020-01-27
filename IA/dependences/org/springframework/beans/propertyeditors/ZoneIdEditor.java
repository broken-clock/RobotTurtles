// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import java.time.ZoneId;
import java.beans.PropertyEditorSupport;

public class ZoneIdEditor extends PropertyEditorSupport
{
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        this.setValue(ZoneId.of(text));
    }
    
    @Override
    public String getAsText() {
        final ZoneId value = (ZoneId)this.getValue();
        return (value != null) ? value.getId() : "";
    }
}
