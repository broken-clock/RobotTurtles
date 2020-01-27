// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import org.springframework.util.Assert;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.ConversionService;
import java.beans.PropertyEditorSupport;

public class ConvertingPropertyEditorAdapter extends PropertyEditorSupport
{
    private final ConversionService conversionService;
    private final TypeDescriptor targetDescriptor;
    private final boolean canConvertToString;
    
    public ConvertingPropertyEditorAdapter(final ConversionService conversionService, final TypeDescriptor targetDescriptor) {
        Assert.notNull(conversionService, "ConversionService must not be null");
        Assert.notNull(targetDescriptor, "TypeDescriptor must not be null");
        this.conversionService = conversionService;
        this.targetDescriptor = targetDescriptor;
        this.canConvertToString = conversionService.canConvert(this.targetDescriptor, TypeDescriptor.valueOf(String.class));
    }
    
    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        this.setValue(this.conversionService.convert(text, TypeDescriptor.valueOf(String.class), this.targetDescriptor));
    }
    
    @Override
    public String getAsText() {
        if (this.canConvertToString) {
            return (String)this.conversionService.convert(this.getValue(), this.targetDescriptor, TypeDescriptor.valueOf(String.class));
        }
        return null;
    }
}
