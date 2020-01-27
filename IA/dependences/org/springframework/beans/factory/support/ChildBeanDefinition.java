// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.util.ObjectUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.MutablePropertyValues;

public class ChildBeanDefinition extends AbstractBeanDefinition
{
    private String parentName;
    
    public ChildBeanDefinition(final String parentName) {
        this.parentName = parentName;
    }
    
    public ChildBeanDefinition(final String parentName, final MutablePropertyValues pvs) {
        super(null, pvs);
        this.parentName = parentName;
    }
    
    public ChildBeanDefinition(final String parentName, final ConstructorArgumentValues cargs, final MutablePropertyValues pvs) {
        super(cargs, pvs);
        this.parentName = parentName;
    }
    
    public ChildBeanDefinition(final String parentName, final Class<?> beanClass, final ConstructorArgumentValues cargs, final MutablePropertyValues pvs) {
        super(cargs, pvs);
        this.parentName = parentName;
        this.setBeanClass(beanClass);
    }
    
    public ChildBeanDefinition(final String parentName, final String beanClassName, final ConstructorArgumentValues cargs, final MutablePropertyValues pvs) {
        super(cargs, pvs);
        this.parentName = parentName;
        this.setBeanClassName(beanClassName);
    }
    
    public ChildBeanDefinition(final ChildBeanDefinition original) {
        super(original);
    }
    
    @Override
    public void setParentName(final String parentName) {
        this.parentName = parentName;
    }
    
    @Override
    public String getParentName() {
        return this.parentName;
    }
    
    @Override
    public void validate() throws BeanDefinitionValidationException {
        super.validate();
        if (this.parentName == null) {
            throw new BeanDefinitionValidationException("'parentName' must be set in ChildBeanDefinition");
        }
    }
    
    @Override
    public AbstractBeanDefinition cloneBeanDefinition() {
        return new ChildBeanDefinition(this);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ChildBeanDefinition)) {
            return false;
        }
        final ChildBeanDefinition that = (ChildBeanDefinition)other;
        return ObjectUtils.nullSafeEquals(this.parentName, that.parentName) && super.equals(other);
    }
    
    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.parentName) * 29 + super.hashCode();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Child bean with parent '");
        sb.append(this.parentName).append("': ").append(super.toString());
        return sb.toString();
    }
}
