// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import java.io.IOException;
import org.xml.sax.InputSource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.core.io.ResourceEditor;
import java.beans.PropertyEditorSupport;

public class InputSourceEditor extends PropertyEditorSupport
{
    private final ResourceEditor resourceEditor;
    
    public InputSourceEditor() {
        this.resourceEditor = new ResourceEditor();
    }
    
    public InputSourceEditor(final ResourceEditor resourceEditor) {
        Assert.notNull(resourceEditor, "ResourceEditor must not be null");
        this.resourceEditor = resourceEditor;
    }
    
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        this.resourceEditor.setAsText(text);
        final Resource resource = (Resource)this.resourceEditor.getValue();
        try {
            this.setValue((resource != null) ? new InputSource(resource.getURL().toString()) : null);
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("Could not retrieve URL for " + resource + ": " + ex.getMessage());
        }
    }
    
    @Override
    public String getAsText() {
        final InputSource value = (InputSource)this.getValue();
        return (value != null) ? value.getSystemId() : "";
    }
}
