// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.multipart.support;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import java.beans.PropertyEditorSupport;

public class StringMultipartFileEditor extends PropertyEditorSupport
{
    private final String charsetName;
    
    public StringMultipartFileEditor() {
        this.charsetName = null;
    }
    
    public StringMultipartFileEditor(final String charsetName) {
        this.charsetName = charsetName;
    }
    
    @Override
    public void setAsText(final String text) {
        this.setValue(text);
    }
    
    @Override
    public void setValue(final Object value) {
        if (value instanceof MultipartFile) {
            final MultipartFile multipartFile = (MultipartFile)value;
            try {
                super.setValue((this.charsetName != null) ? new String(multipartFile.getBytes(), this.charsetName) : new String(multipartFile.getBytes()));
            }
            catch (IOException ex) {
                throw new IllegalArgumentException("Cannot read contents of multipart file", ex);
            }
        }
        else {
            super.setValue(value);
        }
    }
}
