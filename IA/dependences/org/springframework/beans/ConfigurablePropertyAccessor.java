// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import org.springframework.core.convert.ConversionService;

public interface ConfigurablePropertyAccessor extends PropertyAccessor, PropertyEditorRegistry, TypeConverter
{
    void setConversionService(final ConversionService p0);
    
    ConversionService getConversionService();
    
    void setExtractOldValueForEditor(final boolean p0);
    
    boolean isExtractOldValueForEditor();
}
