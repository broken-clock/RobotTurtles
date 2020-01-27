// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;

public class ByteArrayPropertyEditor extends PropertyEditorSupport
{
    @Override
    public void setAsText(final String text) {
        this.setValue((text != null) ? text.getBytes() : null);
    }
    
    @Override
    public String getAsText() {
        final byte[] value = (byte[])this.getValue();
        return (value != null) ? new String(value) : "";
    }
}
