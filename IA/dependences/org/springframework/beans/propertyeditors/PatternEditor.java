// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import java.util.regex.Pattern;
import java.beans.PropertyEditorSupport;

public class PatternEditor extends PropertyEditorSupport
{
    private final int flags;
    
    public PatternEditor() {
        this.flags = 0;
    }
    
    public PatternEditor(final int flags) {
        this.flags = flags;
    }
    
    @Override
    public void setAsText(final String text) {
        this.setValue((text != null) ? Pattern.compile(text, this.flags) : null);
    }
    
    @Override
    public String getAsText() {
        final Pattern value = (Pattern)this.getValue();
        return (value != null) ? value.pattern() : "";
    }
}
