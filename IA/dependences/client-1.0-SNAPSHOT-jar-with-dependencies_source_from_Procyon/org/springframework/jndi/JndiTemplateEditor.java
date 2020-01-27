// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jndi;

import java.util.Properties;
import org.springframework.beans.propertyeditors.PropertiesEditor;
import java.beans.PropertyEditorSupport;

public class JndiTemplateEditor extends PropertyEditorSupport
{
    private final PropertiesEditor propertiesEditor;
    
    public JndiTemplateEditor() {
        this.propertiesEditor = new PropertiesEditor();
    }
    
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        if (text == null) {
            throw new IllegalArgumentException("JndiTemplate cannot be created from null string");
        }
        if ("".equals(text)) {
            this.setValue(new JndiTemplate());
        }
        else {
            this.propertiesEditor.setAsText(text);
            final Properties props = (Properties)this.propertiesEditor.getValue();
            this.setValue(new JndiTemplate(props));
        }
    }
}
