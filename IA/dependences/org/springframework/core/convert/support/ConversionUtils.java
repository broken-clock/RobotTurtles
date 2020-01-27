// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

abstract class ConversionUtils
{
    public static Object invokeConverter(final GenericConverter converter, final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        try {
            return converter.convert(source, sourceType, targetType);
        }
        catch (ConversionFailedException ex) {
            throw ex;
        }
        catch (Exception ex2) {
            throw new ConversionFailedException(sourceType, targetType, source, ex2);
        }
    }
    
    public static boolean canConvertElements(final TypeDescriptor sourceElementType, final TypeDescriptor targetElementType, final ConversionService conversionService) {
        return targetElementType == null || sourceElementType == null || conversionService.canConvert(sourceElementType, targetElementType) || sourceElementType.getType().isAssignableFrom(targetElementType.getType());
    }
}
