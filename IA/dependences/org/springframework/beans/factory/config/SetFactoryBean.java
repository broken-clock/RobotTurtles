// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import java.util.Iterator;
import org.springframework.beans.TypeConverter;
import java.util.Collection;
import org.springframework.core.GenericCollectionTypeResolver;
import java.util.LinkedHashSet;
import org.springframework.beans.BeanUtils;
import java.util.Set;

public class SetFactoryBean extends AbstractFactoryBean<Set<Object>>
{
    private Set<?> sourceSet;
    private Class<? extends Set> targetSetClass;
    
    public void setSourceSet(final Set<?> sourceSet) {
        this.sourceSet = sourceSet;
    }
    
    public void setTargetSetClass(final Class<? extends Set> targetSetClass) {
        if (targetSetClass == null) {
            throw new IllegalArgumentException("'targetSetClass' must not be null");
        }
        if (!Set.class.isAssignableFrom(targetSetClass)) {
            throw new IllegalArgumentException("'targetSetClass' must implement [java.util.Set]");
        }
        this.targetSetClass = targetSetClass;
    }
    
    @Override
    public Class<Set> getObjectType() {
        return Set.class;
    }
    
    @Override
    protected Set<Object> createInstance() {
        if (this.sourceSet == null) {
            throw new IllegalArgumentException("'sourceSet' is required");
        }
        Set<Object> result = null;
        if (this.targetSetClass != null) {
            result = BeanUtils.instantiateClass(this.targetSetClass);
        }
        else {
            result = new LinkedHashSet<Object>(this.sourceSet.size());
        }
        Class<?> valueType = null;
        if (this.targetSetClass != null) {
            valueType = GenericCollectionTypeResolver.getCollectionType(this.targetSetClass);
        }
        if (valueType != null) {
            final TypeConverter converter = this.getBeanTypeConverter();
            for (final Object elem : this.sourceSet) {
                result.add(converter.convertIfNecessary(elem, valueType));
            }
        }
        else {
            result.addAll(this.sourceSet);
        }
        return result;
    }
}
