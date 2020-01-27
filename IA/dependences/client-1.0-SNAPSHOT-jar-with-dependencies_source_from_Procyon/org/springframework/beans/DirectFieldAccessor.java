// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConverterNotFoundException;
import java.beans.PropertyChangeEvent;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.Assert;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.util.Map;

public class DirectFieldAccessor extends AbstractPropertyAccessor
{
    private final Object target;
    private final Map<String, Field> fieldMap;
    
    public DirectFieldAccessor(final Object target) {
        this.fieldMap = new HashMap<String, Field>();
        Assert.notNull(target, "Target object must not be null");
        this.target = target;
        ReflectionUtils.doWithFields(this.target.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(final Field field) {
                if (!DirectFieldAccessor.this.fieldMap.containsKey(field.getName())) {
                    DirectFieldAccessor.this.fieldMap.put(field.getName(), field);
                }
            }
        });
        this.typeConverterDelegate = new TypeConverterDelegate(this, target);
        this.registerDefaultEditors();
        this.setExtractOldValueForEditor(true);
    }
    
    @Override
    public boolean isReadableProperty(final String propertyName) throws BeansException {
        return this.fieldMap.containsKey(propertyName);
    }
    
    @Override
    public boolean isWritableProperty(final String propertyName) throws BeansException {
        return this.fieldMap.containsKey(propertyName);
    }
    
    @Override
    public Class<?> getPropertyType(final String propertyName) throws BeansException {
        final Field field = this.fieldMap.get(propertyName);
        if (field != null) {
            return field.getType();
        }
        return null;
    }
    
    @Override
    public TypeDescriptor getPropertyTypeDescriptor(final String propertyName) throws BeansException {
        final Field field = this.fieldMap.get(propertyName);
        if (field != null) {
            return new TypeDescriptor(field);
        }
        return null;
    }
    
    @Override
    public Object getPropertyValue(final String propertyName) throws BeansException {
        final Field field = this.fieldMap.get(propertyName);
        if (field == null) {
            throw new NotReadablePropertyException(this.target.getClass(), propertyName, "Field '" + propertyName + "' does not exist");
        }
        try {
            ReflectionUtils.makeAccessible(field);
            return field.get(this.target);
        }
        catch (IllegalAccessException ex) {
            throw new InvalidPropertyException(this.target.getClass(), propertyName, "Field is not accessible", ex);
        }
    }
    
    @Override
    public void setPropertyValue(final String propertyName, final Object newValue) throws BeansException {
        final Field field = this.fieldMap.get(propertyName);
        if (field == null) {
            throw new NotWritablePropertyException(this.target.getClass(), propertyName, "Field '" + propertyName + "' does not exist");
        }
        Object oldValue = null;
        try {
            ReflectionUtils.makeAccessible(field);
            oldValue = field.get(this.target);
            final Object convertedValue = this.typeConverterDelegate.convertIfNecessary(field.getName(), oldValue, newValue, field.getType(), new TypeDescriptor(field));
            field.set(this.target, convertedValue);
        }
        catch (ConverterNotFoundException ex) {
            final PropertyChangeEvent pce = new PropertyChangeEvent(this.target, propertyName, oldValue, newValue);
            throw new ConversionNotSupportedException(pce, field.getType(), ex);
        }
        catch (ConversionException ex2) {
            final PropertyChangeEvent pce = new PropertyChangeEvent(this.target, propertyName, oldValue, newValue);
            throw new TypeMismatchException(pce, field.getType(), ex2);
        }
        catch (IllegalStateException ex3) {
            final PropertyChangeEvent pce = new PropertyChangeEvent(this.target, propertyName, oldValue, newValue);
            throw new ConversionNotSupportedException(pce, field.getType(), ex3);
        }
        catch (IllegalArgumentException ex4) {
            final PropertyChangeEvent pce = new PropertyChangeEvent(this.target, propertyName, oldValue, newValue);
            throw new TypeMismatchException(pce, field.getType(), ex4);
        }
        catch (IllegalAccessException ex5) {
            throw new InvalidPropertyException(this.target.getClass(), propertyName, "Field is not accessible", ex5);
        }
    }
}
