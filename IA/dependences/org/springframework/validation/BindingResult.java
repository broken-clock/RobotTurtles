// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation;

import org.springframework.beans.PropertyEditorRegistry;
import java.beans.PropertyEditor;
import java.util.Map;

public interface BindingResult extends Errors
{
    public static final String MODEL_KEY_PREFIX = BindingResult.class.getName() + ".";
    
    Object getTarget();
    
    Map<String, Object> getModel();
    
    Object getRawFieldValue(final String p0);
    
    PropertyEditor findEditor(final String p0, final Class<?> p1);
    
    PropertyEditorRegistry getPropertyEditorRegistry();
    
    void addError(final ObjectError p0);
    
    String[] resolveMessageCodes(final String p0);
    
    String[] resolveMessageCodes(final String p0, final String p1);
    
    void recordSuppressedField(final String p0);
    
    String[] getSuppressedFields();
}
