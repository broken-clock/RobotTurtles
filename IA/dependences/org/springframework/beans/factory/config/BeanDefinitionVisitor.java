// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import org.springframework.beans.PropertyValue;
import org.springframework.util.ObjectUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.util.Assert;
import org.springframework.util.StringValueResolver;

public class BeanDefinitionVisitor
{
    private StringValueResolver valueResolver;
    
    public BeanDefinitionVisitor(final StringValueResolver valueResolver) {
        Assert.notNull(valueResolver, "StringValueResolver must not be null");
        this.valueResolver = valueResolver;
    }
    
    protected BeanDefinitionVisitor() {
    }
    
    public void visitBeanDefinition(final BeanDefinition beanDefinition) {
        this.visitParentName(beanDefinition);
        this.visitBeanClassName(beanDefinition);
        this.visitFactoryBeanName(beanDefinition);
        this.visitFactoryMethodName(beanDefinition);
        this.visitScope(beanDefinition);
        this.visitPropertyValues(beanDefinition.getPropertyValues());
        final ConstructorArgumentValues cas = beanDefinition.getConstructorArgumentValues();
        this.visitIndexedArgumentValues(cas.getIndexedArgumentValues());
        this.visitGenericArgumentValues(cas.getGenericArgumentValues());
    }
    
    protected void visitParentName(final BeanDefinition beanDefinition) {
        final String parentName = beanDefinition.getParentName();
        if (parentName != null) {
            final String resolvedName = this.resolveStringValue(parentName);
            if (!parentName.equals(resolvedName)) {
                beanDefinition.setParentName(resolvedName);
            }
        }
    }
    
    protected void visitBeanClassName(final BeanDefinition beanDefinition) {
        final String beanClassName = beanDefinition.getBeanClassName();
        if (beanClassName != null) {
            final String resolvedName = this.resolveStringValue(beanClassName);
            if (!beanClassName.equals(resolvedName)) {
                beanDefinition.setBeanClassName(resolvedName);
            }
        }
    }
    
    protected void visitFactoryBeanName(final BeanDefinition beanDefinition) {
        final String factoryBeanName = beanDefinition.getFactoryBeanName();
        if (factoryBeanName != null) {
            final String resolvedName = this.resolveStringValue(factoryBeanName);
            if (!factoryBeanName.equals(resolvedName)) {
                beanDefinition.setFactoryBeanName(resolvedName);
            }
        }
    }
    
    protected void visitFactoryMethodName(final BeanDefinition beanDefinition) {
        final String factoryMethodName = beanDefinition.getFactoryMethodName();
        if (factoryMethodName != null) {
            final String resolvedName = this.resolveStringValue(factoryMethodName);
            if (!factoryMethodName.equals(resolvedName)) {
                beanDefinition.setFactoryMethodName(resolvedName);
            }
        }
    }
    
    protected void visitScope(final BeanDefinition beanDefinition) {
        final String scope = beanDefinition.getScope();
        if (scope != null) {
            final String resolvedScope = this.resolveStringValue(scope);
            if (!scope.equals(resolvedScope)) {
                beanDefinition.setScope(resolvedScope);
            }
        }
    }
    
    protected void visitPropertyValues(final MutablePropertyValues pvs) {
        final PropertyValue[] propertyValues;
        final PropertyValue[] pvArray = propertyValues = pvs.getPropertyValues();
        for (final PropertyValue pv : propertyValues) {
            final Object newVal = this.resolveValue(pv.getValue());
            if (!ObjectUtils.nullSafeEquals(newVal, pv.getValue())) {
                pvs.add(pv.getName(), newVal);
            }
        }
    }
    
    protected void visitIndexedArgumentValues(final Map<Integer, ConstructorArgumentValues.ValueHolder> ias) {
        for (final ConstructorArgumentValues.ValueHolder valueHolder : ias.values()) {
            final Object newVal = this.resolveValue(valueHolder.getValue());
            if (!ObjectUtils.nullSafeEquals(newVal, valueHolder.getValue())) {
                valueHolder.setValue(newVal);
            }
        }
    }
    
    protected void visitGenericArgumentValues(final List<ConstructorArgumentValues.ValueHolder> gas) {
        for (final ConstructorArgumentValues.ValueHolder valueHolder : gas) {
            final Object newVal = this.resolveValue(valueHolder.getValue());
            if (!ObjectUtils.nullSafeEquals(newVal, valueHolder.getValue())) {
                valueHolder.setValue(newVal);
            }
        }
    }
    
    protected Object resolveValue(final Object value) {
        if (value instanceof BeanDefinition) {
            this.visitBeanDefinition((BeanDefinition)value);
        }
        else if (value instanceof BeanDefinitionHolder) {
            this.visitBeanDefinition(((BeanDefinitionHolder)value).getBeanDefinition());
        }
        else if (value instanceof RuntimeBeanReference) {
            final RuntimeBeanReference ref = (RuntimeBeanReference)value;
            final String newBeanName = this.resolveStringValue(ref.getBeanName());
            if (!newBeanName.equals(ref.getBeanName())) {
                return new RuntimeBeanReference(newBeanName);
            }
        }
        else if (value instanceof RuntimeBeanNameReference) {
            final RuntimeBeanNameReference ref2 = (RuntimeBeanNameReference)value;
            final String newBeanName = this.resolveStringValue(ref2.getBeanName());
            if (!newBeanName.equals(ref2.getBeanName())) {
                return new RuntimeBeanNameReference(newBeanName);
            }
        }
        else if (value instanceof Object[]) {
            this.visitArray((Object[])value);
        }
        else if (value instanceof List) {
            this.visitList((List)value);
        }
        else if (value instanceof Set) {
            this.visitSet((Set)value);
        }
        else if (value instanceof Map) {
            this.visitMap((Map<?, ?>)value);
        }
        else if (value instanceof TypedStringValue) {
            final TypedStringValue typedStringValue = (TypedStringValue)value;
            final String stringValue = typedStringValue.getValue();
            if (stringValue != null) {
                final String visitedString = this.resolveStringValue(stringValue);
                typedStringValue.setValue(visitedString);
            }
        }
        else if (value instanceof String) {
            return this.resolveStringValue((String)value);
        }
        return value;
    }
    
    protected void visitArray(final Object[] arrayVal) {
        for (int i = 0; i < arrayVal.length; ++i) {
            final Object elem = arrayVal[i];
            final Object newVal = this.resolveValue(elem);
            if (!ObjectUtils.nullSafeEquals(newVal, elem)) {
                arrayVal[i] = newVal;
            }
        }
    }
    
    protected void visitList(final List listVal) {
        for (int i = 0; i < listVal.size(); ++i) {
            final Object elem = listVal.get(i);
            final Object newVal = this.resolveValue(elem);
            if (!ObjectUtils.nullSafeEquals(newVal, elem)) {
                listVal.set(i, newVal);
            }
        }
    }
    
    protected void visitSet(final Set setVal) {
        final Set newContent = new LinkedHashSet();
        boolean entriesModified = false;
        for (final Object elem : setVal) {
            final int elemHash = (elem != null) ? elem.hashCode() : 0;
            final Object newVal = this.resolveValue(elem);
            final int newValHash = (newVal != null) ? newVal.hashCode() : 0;
            newContent.add(newVal);
            entriesModified = (entriesModified || newVal != elem || newValHash != elemHash);
        }
        if (entriesModified) {
            setVal.clear();
            setVal.addAll(newContent);
        }
    }
    
    protected void visitMap(final Map<?, ?> mapVal) {
        final Map newContent = new LinkedHashMap();
        boolean entriesModified = false;
        for (final Map.Entry entry : mapVal.entrySet()) {
            final Object key = entry.getKey();
            final int keyHash = (key != null) ? key.hashCode() : 0;
            final Object newKey = this.resolveValue(key);
            final int newKeyHash = (newKey != null) ? newKey.hashCode() : 0;
            final Object val = entry.getValue();
            final Object newVal = this.resolveValue(val);
            newContent.put(newKey, newVal);
            entriesModified = (entriesModified || newVal != val || newKey != key || newKeyHash != keyHash);
        }
        if (entriesModified) {
            mapVal.clear();
            mapVal.putAll(newContent);
        }
    }
    
    protected String resolveStringValue(final String strVal) {
        if (this.valueResolver == null) {
            throw new IllegalStateException("No StringValueResolver specified - pass a resolver object into the constructor or override the 'resolveStringValue' method");
        }
        final String resolvedValue = this.valueResolver.resolveStringValue(strVal);
        return strVal.equals(resolvedValue) ? strVal : resolvedValue;
    }
}
