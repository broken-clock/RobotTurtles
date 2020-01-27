// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import java.nio.charset.Charset;
import org.springframework.util.StringUtils;
import java.beans.PropertyEditorSupport;

public class CharsetEditor extends PropertyEditorSupport
{
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            this.setValue(Charset.forName(text));
        }
        else {
            this.setValue(null);
        }
    }
    
    @Override
    public String getAsText() {
        final Charset value = (Charset)this.getValue();
        return (value != null) ? value.name() : "";
    }
}
