// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConverterNotFoundException;
import java.lang.reflect.Field;
import org.springframework.core.MethodParameter;

public abstract class TypeConverterSupport extends PropertyEditorRegistrySupport implements TypeConverter
{
    TypeConverterDelegate typeConverterDelegate;
    
    @Override
    public <T> T convertIfNecessary(final Object value, final Class<T> requiredType) throws TypeMismatchException {
        return this.doConvert(value, requiredType, null, null);
    }
    
    @Override
    public <T> T convertIfNecessary(final Object value, final Class<T> requiredType, final MethodParameter methodParam) throws TypeMismatchException {
        return this.doConvert(value, requiredType, methodParam, null);
    }
    
    @Override
    public <T> T convertIfNecessary(final Object value, final Class<T> requiredType, final Field field) throws TypeMismatchException {
        return this.doConvert(value, requiredType, null, field);
    }
    
    private <T> T doConvert(final Object value, final Class<T> requiredType, final MethodParameter methodParam, final Field field) throws TypeMismatchException {
        try {
            if (field != null) {
                return this.typeConverterDelegate.convertIfNecessary(value, requiredType, field);
            }
            return this.typeConverterDelegate.convertIfNecessary(value, requiredType, methodParam);
        }
        catch (ConverterNotFoundException ex) {
            throw new ConversionNotSupportedException(value, requiredType, ex);
        }
        catch (ConversionException ex2) {
            throw new TypeMismatchException(value, requiredType, ex2);
        }
        catch (IllegalStateException ex3) {
            throw new ConversionNotSupportedException(value, requiredType, ex3);
        }
        catch (IllegalArgumentException ex4) {
            throw new TypeMismatchException(value, requiredType, ex4);
        }
    }
}
