// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation;

import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.core.convert.support.ConvertingPropertyEditorAdapter;
import org.springframework.beans.BeanUtils;
import java.beans.PropertyEditor;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.util.Assert;
import org.springframework.core.convert.ConversionService;

public abstract class AbstractPropertyBindingResult extends AbstractBindingResult
{
    private ConversionService conversionService;
    
    protected AbstractPropertyBindingResult(final String objectName) {
        super(objectName);
    }
    
    public void initConversion(final ConversionService conversionService) {
        Assert.notNull(conversionService, "ConversionService must not be null");
        this.conversionService = conversionService;
        if (this.getTarget() != null) {
            this.getPropertyAccessor().setConversionService(conversionService);
        }
    }
    
    @Override
    public PropertyEditorRegistry getPropertyEditorRegistry() {
        return this.getPropertyAccessor();
    }
    
    @Override
    protected String canonicalFieldName(final String field) {
        return PropertyAccessorUtils.canonicalPropertyName(field);
    }
    
    @Override
    public Class<?> getFieldType(final String field) {
        return this.getPropertyAccessor().getPropertyType(this.fixedField(field));
    }
    
    @Override
    protected Object getActualFieldValue(final String field) {
        return this.getPropertyAccessor().getPropertyValue(field);
    }
    
    @Override
    protected Object formatFieldValue(final String field, final Object value) {
        final String fixedField = this.fixedField(field);
        final PropertyEditor customEditor = this.getCustomEditor(fixedField);
        if (customEditor != null) {
            customEditor.setValue(value);
            final String textValue = customEditor.getAsText();
            if (textValue != null) {
                return textValue;
            }
        }
        if (this.conversionService != null) {
            final TypeDescriptor fieldDesc = this.getPropertyAccessor().getPropertyTypeDescriptor(fixedField);
            final TypeDescriptor strDesc = TypeDescriptor.valueOf(String.class);
            if (fieldDesc != null && this.conversionService.canConvert(fieldDesc, strDesc)) {
                return this.conversionService.convert(value, fieldDesc, strDesc);
            }
        }
        return value;
    }
    
    protected PropertyEditor getCustomEditor(final String fixedField) {
        final Class<?> targetType = this.getPropertyAccessor().getPropertyType(fixedField);
        PropertyEditor editor = this.getPropertyAccessor().findCustomEditor(targetType, fixedField);
        if (editor == null) {
            editor = BeanUtils.findEditorByConvention(targetType);
        }
        return editor;
    }
    
    @Override
    public PropertyEditor findEditor(final String field, final Class<?> valueType) {
        Class<?> valueTypeForLookup = valueType;
        if (valueTypeForLookup == null) {
            valueTypeForLookup = this.getFieldType(field);
        }
        PropertyEditor editor = super.findEditor(field, valueTypeForLookup);
        if (editor == null && this.conversionService != null) {
            TypeDescriptor td = null;
            if (field != null) {
                final TypeDescriptor ptd = this.getPropertyAccessor().getPropertyTypeDescriptor(this.fixedField(field));
                if (valueType == null || valueType.isAssignableFrom(ptd.getType())) {
                    td = ptd;
                }
            }
            if (td == null) {
                td = TypeDescriptor.valueOf(valueTypeForLookup);
            }
            if (this.conversionService.canConvert(TypeDescriptor.valueOf(String.class), td)) {
                editor = new ConvertingPropertyEditorAdapter(this.conversionService, td);
            }
        }
        return editor;
    }
    
    public abstract ConfigurablePropertyAccessor getPropertyAccessor();
}
