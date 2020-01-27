// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import java.util.Map;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.Properties;
import java.beans.PropertyEditorSupport;

public class PropertiesEditor extends PropertyEditorSupport
{
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        final Properties props = new Properties();
        if (text != null) {
            try {
                props.load(new ByteArrayInputStream(text.getBytes("ISO-8859-1")));
            }
            catch (IOException ex) {
                throw new IllegalArgumentException("Failed to parse [" + text + "] into Properties", ex);
            }
        }
        this.setValue(props);
    }
    
    @Override
    public void setValue(final Object value) {
        if (!(value instanceof Properties) && value instanceof Map) {
            final Properties props = new Properties();
            props.putAll((Map<?, ?>)value);
            super.setValue(props);
        }
        else {
            super.setValue(value);
        }
    }
}
