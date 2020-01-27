// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import java.util.Iterator;
import org.springframework.beans.TypeConverter;
import java.util.Collection;
import org.springframework.core.GenericCollectionTypeResolver;
import java.util.ArrayList;
import org.springframework.beans.BeanUtils;
import java.util.List;

public class ListFactoryBean extends AbstractFactoryBean<List<Object>>
{
    private List<?> sourceList;
    private Class<? extends List> targetListClass;
    
    public void setSourceList(final List<?> sourceList) {
        this.sourceList = sourceList;
    }
    
    public void setTargetListClass(final Class<? extends List> targetListClass) {
        if (targetListClass == null) {
            throw new IllegalArgumentException("'targetListClass' must not be null");
        }
        if (!List.class.isAssignableFrom(targetListClass)) {
            throw new IllegalArgumentException("'targetListClass' must implement [java.util.List]");
        }
        this.targetListClass = targetListClass;
    }
    
    @Override
    public Class<List> getObjectType() {
        return List.class;
    }
    
    @Override
    protected List<Object> createInstance() {
        if (this.sourceList == null) {
            throw new IllegalArgumentException("'sourceList' is required");
        }
        List<Object> result = null;
        if (this.targetListClass != null) {
            result = BeanUtils.instantiateClass(this.targetListClass);
        }
        else {
            result = new ArrayList<Object>(this.sourceList.size());
        }
        Class<?> valueType = null;
        if (this.targetListClass != null) {
            valueType = GenericCollectionTypeResolver.getCollectionType(this.targetListClass);
        }
        if (valueType != null) {
            final TypeConverter converter = this.getBeanTypeConverter();
            for (final Object elem : this.sourceList) {
                result.add(converter.convertIfNecessary(elem, valueType));
            }
        }
        else {
            result.addAll(this.sourceList);
        }
        return result;
    }
}
