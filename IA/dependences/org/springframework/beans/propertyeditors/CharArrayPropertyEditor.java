// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;

public class CharArrayPropertyEditor extends PropertyEditorSupport
{
    @Override
    public void setAsText(final String text) {
        this.setValue((text != null) ? text.toCharArray() : null);
    }
    
    @Override
    public String getAsText() {
        final char[] value = (char[])this.getValue();
        return (value != null) ? new String(value) : "";
    }
}
