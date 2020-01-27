// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import java.io.IOException;
import org.springframework.core.io.Resource;
import java.io.File;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.Assert;
import org.springframework.core.io.ResourceEditor;
import java.beans.PropertyEditorSupport;

public class FileEditor extends PropertyEditorSupport
{
    private final ResourceEditor resourceEditor;
    
    public FileEditor() {
        this.resourceEditor = new ResourceEditor();
    }
    
    public FileEditor(final ResourceEditor resourceEditor) {
        Assert.notNull(resourceEditor, "ResourceEditor must not be null");
        this.resourceEditor = resourceEditor;
    }
    
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        if (!StringUtils.hasText(text)) {
            this.setValue(null);
            return;
        }
        if (!ResourceUtils.isUrl(text)) {
            final File file = new File(text);
            if (file.isAbsolute()) {
                this.setValue(file);
                return;
            }
        }
        this.resourceEditor.setAsText(text);
        final Resource resource = (Resource)this.resourceEditor.getValue();
        Label_0130: {
            if (!ResourceUtils.isUrl(text)) {
                if (!resource.exists()) {
                    break Label_0130;
                }
            }
            try {
                this.setValue(resource.getFile());
                return;
            }
            catch (IOException ex) {
                throw new IllegalArgumentException("Could not retrieve File for " + resource + ": " + ex.getMessage());
            }
        }
        this.setValue(new File(text));
    }
    
    @Override
    public String getAsText() {
        final File value = (File)this.getValue();
        return (value != null) ? value.getPath() : "";
    }
}
