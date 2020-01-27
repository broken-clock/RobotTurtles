// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import org.springframework.util.StringUtils;
import java.beans.PropertyEditorSupport;

public class CustomBooleanEditor extends PropertyEditorSupport
{
    public static final String VALUE_TRUE = "true";
    public static final String VALUE_FALSE = "false";
    public static final String VALUE_ON = "on";
    public static final String VALUE_OFF = "off";
    public static final String VALUE_YES = "yes";
    public static final String VALUE_NO = "no";
    public static final String VALUE_1 = "1";
    public static final String VALUE_0 = "0";
    private final String trueString;
    private final String falseString;
    private final boolean allowEmpty;
    
    public CustomBooleanEditor(final boolean allowEmpty) {
        this(null, null, allowEmpty);
    }
    
    public CustomBooleanEditor(final String trueString, final String falseString, final boolean allowEmpty) {
        this.trueString = trueString;
        this.falseString = falseString;
        this.allowEmpty = allowEmpty;
    }
    
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        final String input = (text != null) ? text.trim() : null;
        if (this.allowEmpty && !StringUtils.hasLength(input)) {
            this.setValue(null);
        }
        else if (this.trueString != null && input.equalsIgnoreCase(this.trueString)) {
            this.setValue(Boolean.TRUE);
        }
        else if (this.falseString != null && input.equalsIgnoreCase(this.falseString)) {
            this.setValue(Boolean.FALSE);
        }
        else if (this.trueString == null && (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("on") || input.equalsIgnoreCase("yes") || input.equals("1"))) {
            this.setValue(Boolean.TRUE);
        }
        else {
            if (this.falseString != null || (!input.equalsIgnoreCase("false") && !input.equalsIgnoreCase("off") && !input.equalsIgnoreCase("no") && !input.equals("0"))) {
                throw new IllegalArgumentException("Invalid boolean value [" + text + "]");
            }
            this.setValue(Boolean.FALSE);
        }
    }
    
    @Override
    public String getAsText() {
        if (Boolean.TRUE.equals(this.getValue())) {
            return (this.trueString != null) ? this.trueString : "true";
        }
        if (Boolean.FALSE.equals(this.getValue())) {
            return (this.falseString != null) ? this.falseString : "false";
        }
        return "";
    }
}
