// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.propertyeditors;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import java.beans.PropertyEditorSupport;

public class StringArrayPropertyEditor extends PropertyEditorSupport
{
    public static final String DEFAULT_SEPARATOR = ",";
    private final String separator;
    private final String charsToDelete;
    private final boolean emptyArrayAsNull;
    private final boolean trimValues;
    
    public StringArrayPropertyEditor() {
        this(",", null, false);
    }
    
    public StringArrayPropertyEditor(final String separator) {
        this(separator, null, false);
    }
    
    public StringArrayPropertyEditor(final String separator, final boolean emptyArrayAsNull) {
        this(separator, null, emptyArrayAsNull);
    }
    
    public StringArrayPropertyEditor(final String separator, final boolean emptyArrayAsNull, final boolean trimValues) {
        this(separator, null, emptyArrayAsNull, trimValues);
    }
    
    public StringArrayPropertyEditor(final String separator, final String charsToDelete, final boolean emptyArrayAsNull) {
        this(separator, charsToDelete, emptyArrayAsNull, true);
    }
    
    public StringArrayPropertyEditor(final String separator, final String charsToDelete, final boolean emptyArrayAsNull, final boolean trimValues) {
        this.separator = separator;
        this.charsToDelete = charsToDelete;
        this.emptyArrayAsNull = emptyArrayAsNull;
        this.trimValues = trimValues;
    }
    
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        String[] array = StringUtils.delimitedListToStringArray(text, this.separator, this.charsToDelete);
        if (this.trimValues) {
            array = StringUtils.trimArrayElements(array);
        }
        if (this.emptyArrayAsNull && array.length == 0) {
            this.setValue(null);
        }
        else {
            this.setValue(array);
        }
    }
    
    @Override
    public String getAsText() {
        return StringUtils.arrayToDelimitedString(ObjectUtils.toObjectArray(this.getValue()), this.separator);
    }
}
