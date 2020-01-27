// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http;

import org.springframework.util.StringUtils;
import java.beans.PropertyEditorSupport;

public class MediaTypeEditor extends PropertyEditorSupport
{
    @Override
    public void setAsText(final String text) {
        if (StringUtils.hasText(text)) {
            this.setValue(MediaType.parseMediaType(text));
        }
        else {
            this.setValue(null);
        }
    }
    
    @Override
    public String getAsText() {
        final MediaType mediaType = (MediaType)this.getValue();
        return (mediaType != null) ? mediaType.toString() : "";
    }
}
