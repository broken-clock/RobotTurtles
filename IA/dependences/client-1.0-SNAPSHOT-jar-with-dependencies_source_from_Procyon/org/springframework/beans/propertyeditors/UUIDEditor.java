// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import java.util.UUID;
import org.springframework.util.StringUtils;
import java.beans.PropertyEditorSupport;

public class UUIDEditor extends PropertyEditorSupport
{
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            this.setValue(UUID.fromString(text));
        }
        else {
            this.setValue(null);
        }
    }
    
    @Override
    public String getAsText() {
        final UUID value = (UUID)this.getValue();
        return (value != null) ? value.toString() : "";
    }
}
