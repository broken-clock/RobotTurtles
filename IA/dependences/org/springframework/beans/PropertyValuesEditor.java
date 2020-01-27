// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import java.util.Map;
import java.util.Properties;
import org.springframework.beans.propertyeditors.PropertiesEditor;
import java.beans.PropertyEditorSupport;

public class PropertyValuesEditor extends PropertyEditorSupport
{
    private final PropertiesEditor propertiesEditor;
    
    public PropertyValuesEditor() {
        this.propertiesEditor = new PropertiesEditor();
    }
    
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        this.propertiesEditor.setAsText(text);
        final Properties props = (Properties)this.propertiesEditor.getValue();
        this.setValue(new MutablePropertyValues(props));
    }
}
