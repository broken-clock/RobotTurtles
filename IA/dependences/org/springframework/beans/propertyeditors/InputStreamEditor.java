// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.core.io.ResourceEditor;
import java.beans.PropertyEditorSupport;

public class InputStreamEditor extends PropertyEditorSupport
{
    private final ResourceEditor resourceEditor;
    
    public InputStreamEditor() {
        this.resourceEditor = new ResourceEditor();
    }
    
    public InputStreamEditor(final ResourceEditor resourceEditor) {
        Assert.notNull(resourceEditor, "ResourceEditor must not be null");
        this.resourceEditor = resourceEditor;
    }
    
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        this.resourceEditor.setAsText(text);
        final Resource resource = (Resource)this.resourceEditor.getValue();
        try {
            this.setValue((resource != null) ? resource.getInputStream() : null);
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("Could not retrieve InputStream for " + resource + ": " + ex.getMessage());
        }
    }
    
    @Override
    public String getAsText() {
        return null;
    }
}
