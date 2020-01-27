// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import org.springframework.util.StringUtils;
import java.beans.PropertyEditorSupport;

public class CharacterEditor extends PropertyEditorSupport
{
    private static final String UNICODE_PREFIX = "\\u";
    private static final int UNICODE_LENGTH = 6;
    private final boolean allowEmpty;
    
    public CharacterEditor(final boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
    }
    
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        if (this.allowEmpty && !StringUtils.hasLength(text)) {
            this.setValue(null);
        }
        else {
            if (text == null) {
                throw new IllegalArgumentException("null String cannot be converted to char type");
            }
            if (this.isUnicodeCharacterSequence(text)) {
                this.setAsUnicode(text);
            }
            else {
                if (text.length() != 1) {
                    throw new IllegalArgumentException("String [" + text + "] with length " + text.length() + " cannot be converted to char type");
                }
                this.setValue(new Character(text.charAt(0)));
            }
        }
    }
    
    @Override
    public String getAsText() {
        final Object value = this.getValue();
        return (value != null) ? value.toString() : "";
    }
    
    private boolean isUnicodeCharacterSequence(final String sequence) {
        return sequence.startsWith("\\u") && sequence.length() == 6;
    }
    
    private void setAsUnicode(final String text) {
        final int code = Integer.parseInt(text.substring("\\u".length()), 16);
        this.setValue(new Character((char)code));
    }
}
