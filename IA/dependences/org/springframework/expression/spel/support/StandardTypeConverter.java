// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression.spel.support;

import org.springframework.core.convert.ConversionException;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.TypeConverter;

public class StandardTypeConverter implements TypeConverter
{
    private static ConversionService defaultConversionService;
    private final ConversionService conversionService;
    
    public StandardTypeConverter() {
        synchronized (this) {
            if (StandardTypeConverter.defaultConversionService == null) {
                StandardTypeConverter.defaultConversionService = new DefaultConversionService();
            }
        }
        this.conversionService = StandardTypeConverter.defaultConversionService;
    }
    
    public StandardTypeConverter(final ConversionService conversionService) {
        Assert.notNull(conversionService, "ConversionService must not be null");
        this.conversionService = conversionService;
    }
    
    @Override
    public boolean canConvert(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        return this.conversionService.canConvert(sourceType, targetType);
    }
    
    @Override
    public Object convertValue(final Object value, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        try {
            return this.conversionService.convert(value, sourceType, targetType);
        }
        catch (ConversionException ex) {
            throw new SpelEvaluationException(ex, SpelMessage.TYPE_CONVERSION_ERROR, new Object[] { sourceType.toString(), targetType.toString() });
        }
    }
}
