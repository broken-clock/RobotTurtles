// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Map;

public abstract class AbstractPropertyAccessor extends TypeConverterSupport implements ConfigurablePropertyAccessor
{
    private boolean extractOldValueForEditor;
    
    public AbstractPropertyAccessor() {
        this.extractOldValueForEditor = false;
    }
    
    @Override
    public void setExtractOldValueForEditor(final boolean extractOldValueForEditor) {
        this.extractOldValueForEditor = extractOldValueForEditor;
    }
    
    @Override
    public boolean isExtractOldValueForEditor() {
        return this.extractOldValueForEditor;
    }
    
    @Override
    public void setPropertyValue(final PropertyValue pv) throws BeansException {
        this.setPropertyValue(pv.getName(), pv.getValue());
    }
    
    @Override
    public void setPropertyValues(final Map<?, ?> map) throws BeansException {
        this.setPropertyValues(new MutablePropertyValues(map));
    }
    
    @Override
    public void setPropertyValues(final PropertyValues pvs) throws BeansException {
        this.setPropertyValues(pvs, false, false);
    }
    
    @Override
    public void setPropertyValues(final PropertyValues pvs, final boolean ignoreUnknown) throws BeansException {
        this.setPropertyValues(pvs, ignoreUnknown, false);
    }
    
    @Override
    public void setPropertyValues(final PropertyValues pvs, final boolean ignoreUnknown, final boolean ignoreInvalid) throws BeansException {
        List<PropertyAccessException> propertyAccessExceptions = null;
        final List<PropertyValue> propertyValues = (pvs instanceof MutablePropertyValues) ? ((MutablePropertyValues)pvs).getPropertyValueList() : Arrays.asList(pvs.getPropertyValues());
        for (final PropertyValue pv : propertyValues) {
            try {
                this.setPropertyValue(pv);
            }
            catch (NotWritablePropertyException ex) {
                if (!ignoreUnknown) {
                    throw ex;
                }
                continue;
            }
            catch (NullValueInNestedPathException ex2) {
                if (!ignoreInvalid) {
                    throw ex2;
                }
                continue;
            }
            catch (PropertyAccessException ex3) {
                if (propertyAccessExceptions == null) {
                    propertyAccessExceptions = new LinkedList<PropertyAccessException>();
                }
                propertyAccessExceptions.add(ex3);
            }
        }
        if (propertyAccessExceptions != null) {
            final PropertyAccessException[] paeArray = propertyAccessExceptions.toArray(new PropertyAccessException[propertyAccessExceptions.size()]);
            throw new PropertyBatchUpdateException(paeArray);
        }
    }
    
    @Override
    public Class<?> getPropertyType(final String propertyPath) {
        return null;
    }
    
    @Override
    public abstract Object getPropertyValue(final String p0) throws BeansException;
    
    @Override
    public abstract void setPropertyValue(final String p0, final Object p1) throws BeansException;
}
