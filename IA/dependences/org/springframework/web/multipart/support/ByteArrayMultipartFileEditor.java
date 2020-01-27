// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.multipart.support;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.propertyeditors.ByteArrayPropertyEditor;

public class ByteArrayMultipartFileEditor extends ByteArrayPropertyEditor
{
    @Override
    public void setValue(final Object value) {
        if (value instanceof MultipartFile) {
            final MultipartFile multipartFile = (MultipartFile)value;
            try {
                super.setValue(multipartFile.getBytes());
            }
            catch (IOException ex) {
                throw new IllegalArgumentException("Cannot read contents of multipart file", ex);
            }
        }
        else if (value instanceof byte[]) {
            super.setValue(value);
        }
        else {
            super.setValue((value != null) ? value.toString().getBytes() : null);
        }
    }
    
    @Override
    public String getAsText() {
        final byte[] value = (byte[])this.getValue();
        return (value != null) ? new String(value) : "";
    }
}
